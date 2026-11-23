//Shattered Lab glow: keeps only the bright parts of glowing objects
#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main() {
    vec3 c = texture2D(u_texture, v_texCoords).rgb;
    gl_FragColor = vec4(max((c - vec3(0.8)) * 2.5, vec3(0.0)), 1.0);
}
