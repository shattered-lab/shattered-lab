//Shattered Lab screen pass: glow plus screen colour (low health, invisibility, mind vision,
//poison, starving, teleport, the amulet's curse)
#ifdef GL_ES
precision mediump float;
#endif
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform sampler2D u_bloom;
uniform float u_bloomIntensity;
uniform float u_fisheye;
uniform float u_distortion;
uniform float u_time;
uniform float u_chromatic;
uniform float u_saturation;
uniform vec3 u_tint;
uniform float u_tintAmount;
uniform float u_noise;
uniform float u_seed;
uniform float u_vignette;
uniform vec3 u_vignetteColor;

float rand(vec2 co) {
    return fract(sin(dot(co, vec2(12.9898, 78.233)) + u_seed) * 43758.5453);
}

void main() {
    vec2 uv = v_texCoords;

    if (u_fisheye > 0.0001) {
        vec2 xy = 2.0 * uv - 1.0;
        float d = length(xy);
        xy *= 1.0 + u_fisheye * (d * d);
        uv = (xy + 1.0) * 0.5;
    }
    if (uv.x < 0.0 || uv.x > 1.0 || uv.y < 0.0 || uv.y > 1.0) {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        return;
    }
    if (u_distortion > 0.0001) {
        uv.x += sin(uv.y * 20.0 + u_time * 4.0) * u_distortion;
        uv.y += cos(uv.x * 20.0 + u_time * 4.0) * u_distortion;
    }

    vec3 color;
    if (u_chromatic > 0.0001) {
        vec2 offset = (uv - vec2(0.5)) * u_chromatic;
        color = vec3(texture2D(u_texture, uv + offset).r,
                     texture2D(u_texture, uv).g,
                     texture2D(u_texture, uv - offset).b);
    } else {
        color = texture2D(u_texture, uv).rgb;
    }

    vec3 glow = texture2D(u_bloom, uv).rgb * u_bloomIntensity;
    color = color * (vec3(1.0) - clamp(glow, 0.0, 1.0)) + glow;

    float gray = dot(color, vec3(0.299, 0.587, 0.114));
    color = mix(vec3(gray), color, u_saturation);
    color = mix(color, color * u_tint, u_tintAmount);

    if (u_noise > 0.001) {
        color += vec3((rand(uv) - 0.5) * u_noise);
    }

    if (u_vignette > 0.001) {
        float dist = length(uv - vec2(0.5));
        float v = smoothstep(0.2, 0.7, dist * (u_vignette + 0.5));
        vec3 vc = mix(u_vignetteColor, vec3(0.0), 1.0 - v);
        color = mix(color, vc, v * u_vignette);
    }

    gl_FragColor = vec4(color, 1.0);
}
