#version 330 core
uniform mat4 uProjection;
uniform mat4 uView;
uniform float uTime;

out vec4 FragColor;

float sdCircle(vec2 p, float r) {
    return length(p) - r;
}

float sdBox( vec2 p, vec2 b )
{
    vec2 d = abs(p)-b;
    return length(max(d,0.0)) + min(max(d.x,d.y),0.0);
}

void main()
{
//    FragColor = vec4(0.0, 0.0, 1.0, 1.0); // solid blue test
    // gl_FragCoord.xy gives the pixel position in window coordinates
    float x = gl_FragCoord.x;
    float y = gl_FragCoord.y;
    float offsetX = 1300.0f;
    float offsetY = 700.0f;
    float isolineZoom = 0.3f;

    float dist = sdCircle(vec2(x-offsetX, y-offsetY), 300.0f);
//    float dist2 = sdBox(vec2(x-offsetX, y-500.0f), vec2(250.0f, 150.0f));
//
//    float dist = min(dist1, dist2);


    vec3 col = (dist>0.0) ? vec3(0.9,0.6,0.3) : vec3(0.60,0.75,1.0);
    float direction = (dist > 0) ? 5.0f : -5.0f;
    col *= 1.0 - exp(-0.09 * abs(dist));
    col *= 0.8 + 0.2 * cos(0.40*dist + uTime * direction);
    col = mix( col, vec3(1.0), 1.0-smoothstep(0.0,0.01,abs(dist)) );

    FragColor = vec4(col, 1.0f);


}


