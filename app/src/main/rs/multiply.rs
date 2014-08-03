

#pragma version(1)
#pragma rs java_package_name(com.codecraft.renderscriptplayground)
#pragma rs_fp_relaxed

float darkenFactor = 0.f;

uchar4 __attribute__((kernel)) multiply(uchar4 in) {
  uchar4 out = in;
  out.r =  darkenFactor * out.r;
  out.g = darkenFactor * out.g;
  out.b = darkenFactor * out.b;

  return out;
}