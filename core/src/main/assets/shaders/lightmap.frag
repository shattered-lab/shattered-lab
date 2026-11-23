//Shattered Lab dynamic light: the world times the light map, never darker than a dim blue.
//Coloured light is softened slightly toward white, so a fire tints a room without flooding it
#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_lightmap;
void main() {
    vec3 world = texture2D(u_texture, v_texCoords).rgb;
    vec3 light = texture2D(u_lightmap, v_texCoords).rgb;
    float m = max(light.r, max(light.g, light.b));
    light = mix(vec3(m), light, 0.85);
    light = max(light, vec3(0.40, 0.42, 0.50));
    gl_FragColor = vec4(world * light, 1.0);
}
