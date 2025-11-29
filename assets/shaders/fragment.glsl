#version 330 core
//uniform vec2 u_resolution;
out vec4 FragColor;

void main()
{
//    FragColor = vec4(0.0, 0.0, 1.0, 1.0); // solid blue test
    // gl_FragCoord.xy gives the pixel position in window coordinates
    float x = gl_FragCoord.x;
    float y = gl_FragCoord.y;

//    // Normalize coordinates (assuming a 800x600 window)
    float nx = x / 800.0;
    float ny = y / 600.0;
//    if (nx < 0){
//        // Use normalized position to set color
//        FragColor = vec4(0.0, 0.0, 0.5, 1.0);
//    }
//    else {
//        // Use normalized position to set color
//        FragColor = vec4(nx, ny, 0.5, 1.0);
//    }

    if (x < 800.0) {
        FragColor = vec4(0.0, 0.0, 0.5, 1.0);
    }
    else {
        FragColor = vec4(nx, ny, 0.5, 1.0);
    }



}


