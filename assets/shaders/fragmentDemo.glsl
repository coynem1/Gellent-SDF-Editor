#version 330 core
uniform mat4 uProjection;
uniform mat4 uView;
uniform ivec2 uResolution;
uniform vec2 uCamPos;   // World units
uniform float uZoom;
uniform float uViewHeight;

uniform float uTime;
uniform int uDemoScene;
uniform float uBlend;
uniform int uToggleRender;
uniform vec2 uMouse;

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

// sigmoid smoothing
float smin( float a, float b, float k )
{
    k *= log(2.0);
    float x = b-a;
    return a + x/(1.0-exp2(x/k));
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

// SDFs for Hi text
float hiDemo() {
    vec2 world = screenToWorld(gl_FragCoord.xy);
    vec2 offset = vec2(0.0f, 0.0f);
    float xMid = world.x-offset.x;
    float yMid = world.y-offset.y;

    float dCircle1 = sdCircle(vec2(xMid, yMid), 10.0f);
    float dBox1 = sdBox(vec2(xMid, yMid + 50.0f), vec2(8.0f - uBlend, 33.0f - uBlend)) - uBlend;
    float dBox2 = sdBox(vec2(xMid+24, yMid + 33.0f), vec2(8.0f - uBlend, 50.0f - uBlend)) - uBlend;
    float dBox3 = sdBox(vec2(xMid+60, yMid + 33.0f), vec2(8.0f - uBlend, 50.0f - uBlend)) - uBlend;
    float dBox4 = sdBox(vec2(xMid+42, yMid + 33.0f), vec2(20.0f- uBlend, 8.0f -uBlend)) - uBlend;

    return min(min(min(min(dCircle1, dBox1), dBox2), dBox3), dBox4);
}

// Demo of multiple types of shapes
float shapesScene() {
    vec2 world = screenToWorld(gl_FragCoord.xy);
    vec2 offset = vec2(0.0f, 0.0f);
    float scalar = 0.1f;
    float xMid = world.x-offset.x;
    float yMid = world.y-offset.y;

    float dCircle1 = sdCircle(vec2(xMid, yMid), 25.0f);
    float dBox1 = sdBox(vec2(xMid-20, yMid + 40.0f), vec2(10.0f, 10.0f));
    float dBox2 = sdBox(vec2(xMid-60, yMid + 8.0f), vec2(25.0f, 10.0f));
    float dTriangle = sdEquilateralTriangle(vec2(xMid+24, yMid + 70.0f), 40.0f);
    float dStar = sdStar(vec2(xMid+60, yMid), 20.0f + sin(uTime) * 5.0f);


    return min(min(min(smin(dCircle1, dTriangle, 10.0f * uBlend), dBox1), dBox2), dStar);
}

// smooth blend between shapes
float smoothingScene() {
    vec2 world = screenToWorld(gl_FragCoord.xy);
    vec2 offset = vec2(-5f, 0f);
    float xMid = world.x-offset.x;
    float yMid = world.y-offset.y;

    float dCircle1 = sdCircle(vec2(xMid+20, yMid), 40.0f);
    float dBox1 = sdBox(vec2(xMid-50, yMid + 30.0f), vec2(30.0f, 40.0f));

    return smin(dCircle1, dBox1, 5.0f * uBlend);
}

// Renders SDFs with animated isolines
void render(float dist, float zoom, int uDemoScene) {
    vec3 col = (dist>0.0) ? vec3(0.9,0.6,0.3) : vec3(0.60,0.75,1.0);
    float direction = (dist > 0) ? 2.0f : -2.0f;

    // Black and white
    if (uToggleRender == 0) {
        FragColor = (dist<0.0) ? vec4(1.0, 1.0, 1.0, 1.0) : vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    else if (uToggleRender == 2) {
        direction = 0.0f;
    }
    else if (uToggleRender == 3) {
        float feathering = 9.0f;
        FragColor = vec4(1.0-max(dist /feathering, 0.0f), 1.0-max(dist /feathering, 0.0f), 1.0-max(dist /feathering, 0.0f), 1.0);

        return;
    }

    col *= 1.0 - exp(-0.3 * abs(dist));    // Smoothing
    col *= 0.8 + 0.2 * cos(zoom * dist + uTime * direction);    // Isolines
    col = mix( col, vec3(1.0), 1.0 - smoothstep(0.0, 0.60, abs(dist)));    // White outline


    FragColor = vec4(col, 1.0f); //vec4(mod(float(2500f) * 0.0001f, 1.0f), mod(float(m.y) *0.0001f, 1.0f), 0.0f, 1.0f);
}





void main()
{
    float dist = 0.0f;
    vec2 world;

    switch (uDemoScene) {
        case 0:
            world = screenToWorld(gl_FragCoord.xy);
            dist = sdCircle(vec2(world.x, world.y),100.0f);
            break;
        case 1:
            world = screenToWorld(gl_FragCoord.xy);
            dist = sdBox(vec2(world.x, world.y), vec2(100.0f, 100.0f));
            break;
        case 2:
            dist = smoothingScene();
            break;
        case 3:
            dist = shapesScene();
            break;
        case 4:
            dist = hiDemo();
            break;
        default:
            break;
    }
    render(dist, 1.4f, uDemoScene);
}


