//Shattered Lab glow: one direction of a 9 tap blur
#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_weights[9];
uniform vec2 u_offsets[9];
void main() {
    vec4 color = vec4(0.0);
    for (int i = 0; i < 9; i++) {
        color += texture2D(u_texture, v_texCoords + u_offsets[i]) * u_weights[i];
    }
    gl_FragColor = vec4(color.rgb, 1.0);
}
