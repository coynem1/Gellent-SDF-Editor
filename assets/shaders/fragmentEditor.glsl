#version 330 core
uniform mat4 uProjection;
uniform mat4 uView;
uniform ivec2 uResolution;
uniform vec2 uCamPos;   // World units
uniform float uZoom;
uniform float uViewHeight;

uniform float uTime;
uniform int uToggleRender;

out vec4 FragColor;

// Calculates where the camera is
vec2 screenToWorld(vec2 fragCoord) {
    // Convert to Normalised Device Co-ordinates
    vec2 ndc = (fragCoord / vec2(uResolution)) * 2.0 - 1.0;

    float aspectRatio = float(uResolution.x) / float(uResolution.y);
    float viewWidth = uViewHeight * aspectRatio;

    float halfWidth = (viewWidth / 2.0) / uZoom;
    float halfHeight = (uViewHeight / 2.0) / uZoom;

    // Scale NDC to world space and offset by camera position
    vec2 worldPos = ndc * vec2(halfWidth, halfHeight) + uCamPos;

    return worldPos;
}

// cubic smoothing
float smin( float a, float b, float k )
{
    float h = max(k - abs(a - b), 0.0) / k;
    float m = h * h * h * 0.5;
    float s = m * k * (1.0 / 3.0);
    return min(a, b) - s;
}

float sdCircle(vec2 p, float r) {
    return length(p) - r;
}

float sdBox( vec2 p, vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

float sdEquilateralTriangle( vec2 p, float r )
{
    const float k = sqrt(3.0);
    p.x = abs(p.x) - r;
    p.y = p.y + r/k;
    if( p.x+k*p.y>0.0 ) p = vec2(p.x-k*p.y,-k*p.x-p.y)/2.0;
    p.x -= clamp( p.x, -2.0*r, 0.0 );
    return -length(p)*sign(p.y);
}

float sdStar(vec2 p, float r )
{
    const float k1x = 0.809016994; // cos(π/ 5) = ¼(√5+1)
    const float k2x = 0.309016994; // sin(π/10) = ¼(√5-1)
    const float k1y = 0.587785252; // sin(π/ 5) = ¼√(10-2√5)
    const float k2y = 0.951056516; // cos(π/10) = ¼√(10+2√5)
    const float k1z = 0.726542528; // tan(π/ 5) = √(5-2√5)
    const vec2  v1  = vec2( k1x,-k1y);
    const vec2  v2  = vec2(-k1x,-k1y);
    const vec2  v3  = vec2( k2x,-k2y);

    p.x = abs(p.x);
    p -= 2.0*max(dot(v1,p),0.0)*v1;
    p -= 2.0*max(dot(v2,p),0.0)*v2;
    p.x = abs(p.x);
    p.y -= r;
    return length(p-v3*clamp(dot(p,v3),0.0,k1z*r)) * sign(p.y*v3.x-p.x*v3.y);
}

// Renders SDFs with animated isolines
void render(float dist, float zoom) {
    vec3 col = (dist>0.0) ? vec3(0.9,0.6,0.3) : vec3(0.60,0.75,1.0);
    float direction = (dist > 0) ? 2.0 : -2.0;

    // Black and white
    if (uToggleRender == 0) {
        FragColor = (dist<0.0) ? vec4(1.0, 1.0, 1.0, 1.0) : vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    else if (uToggleRender == 2) {
        direction = 0.0;
    }
    else if (uToggleRender == 3) {
        float feathering = 9.0;
        FragColor = vec4(1.0-max(dist /feathering, 0.0), 1.0-max(dist /feathering, 0.0), 1.0-max(dist /feathering, 0.0), 1.0);

        return;
    }

    col *= 1.0 - exp(-0.3 * abs(dist));    // Smoothing
    col *= 0.8 + 0.2 * cos(zoom * dist + uTime * direction);    // Isolines
    col = mix( col, vec3(1.0), 1.0 - smoothstep(0.0, 0.60, abs(dist)));    // White outline


    FragColor = vec4(col, 1.0);
}

#define MAX_SHAPES 145

uniform int uShapeCount;
uniform int uShapeTypes[MAX_SHAPES];
uniform int uShapeModes[MAX_SHAPES];
uniform vec2 uShapePos[MAX_SHAPES];
uniform float uShapeSizes[MAX_SHAPES];
uniform float uShapeAngles[MAX_SHAPES];
uniform float uShapeBlends[MAX_SHAPES];
uniform float uShapeRounds[MAX_SHAPES];

// Rotation function for SDF
vec2 rotate(vec2 p, float angle) {
    float cosA = cos(angle);
    float sinA = sin(angle);
    return mat2(cosA, -sinA, sinA, cosA) * p; // Rotate `p` by `angle`
}

float intersect(float shape1, float shape2){
    return max(shape1, shape2);
}

float difference(float base, float subtraction){
    return intersect(base, -subtraction);
}

float round_merge(float shape1, float shape2, float blend) {
    vec2 intersectionSpace = vec2(shape1 - blend, shape2 - blend);
    intersectionSpace = min(intersectionSpace, 0.0);

    float insideDistance = -length(intersectionSpace);
    float simpleUnion = smin(shape1, shape2, blend);
    float outsideDistance = max(simpleUnion, blend);
    return insideDistance + outsideDistance;
}

float round_intersect(float shape1, float shape2, float radius){
    vec2 intersectionSpace = vec2(shape1 + radius, shape2 + radius);
    intersectionSpace = max(intersectionSpace, 0.0);

    float outsideDistance = length(intersectionSpace);
    float simpleIntersection = intersect(shape1, shape2);
    float insideDistance = min(simpleIntersection, -radius);
    return outsideDistance + insideDistance;
}

float round_subtract(float base, float subtraction, float radius){
    return round_intersect(base, -subtraction, radius);
}

float userScene() {
    vec2 world = screenToWorld(gl_FragCoord.xy);
    float dist = 1e10; // Start with huge distance

    for (int i = 0; i < MAX_SHAPES; i++) {
        if (i >= uShapeCount) break;

        vec2 pos    = uShapePos[i].xy;
        float size  = uShapeSizes[i];
        vec2 p      = world - pos;
        vec2 pRotated = p;

        if (uShapeAngles[i] != 0.0) pRotated = rotate(p, uShapeAngles[i]);

        float d;
        float rounded = uShapeRounds[i] * size; // Rounds edges
        if      (uShapeTypes[i] == 0) d = sdCircle(pRotated, size);
        else if (uShapeTypes[i] == 1) d = sdBox(pRotated, vec2(size - rounded, size - rounded)) - rounded;
        else if (uShapeTypes[i] == 2) d = sdEquilateralTriangle(pRotated, size - rounded) - rounded;
        else if (uShapeTypes[i] == 3) d = sdStar(pRotated, size - rounded) - rounded;

        // What modes are shapes in?
        if (uShapeModes[i] == 0) {
            if (uShapeBlends[i] == 0.0) dist = min(dist, d);
            else dist = round_merge(dist, d, uShapeBlends[i]);
        }
        else if (uShapeModes[i] == 1) {
            if (uShapeBlends[i] == 0.0) dist = difference(dist, d);
            else dist = round_subtract(dist, d, uShapeBlends[i]);
        }
        else {
            if (uShapeBlends[i] == 0.0) dist = intersect(dist, d);
            else dist = round_intersect(dist, d, uShapeBlends[i]);
        }
//        dist = min(dist, d);
    }

    return dist;
}

void main()
{
    float dist = userScene();
    render(dist, 1.4);
}


