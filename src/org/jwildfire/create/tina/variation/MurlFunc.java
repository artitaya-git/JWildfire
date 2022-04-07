/*
  JWildfire - an image and animation processor written in Java
  Copyright (C) 1995-2021 Andreas Maschke

  This is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
  General Public License as published by the Free Software Foundation; either version 2.1 of the
  License, or (at your option) any later version.

  This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public License along with this software;
  if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
  02110-1301 USA, or see the FSF site: http://www.fsf.org.
*/
package org.jwildfire.create.tina.variation;

import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class MurlFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  /*
   Original function written in C by Peter Sdobnov (Zueuk).
   Transcribed into Java by Nic Anderson (chronologicaldot)
   */

  private double _c, _p2, _vp;

  // temp variables (instantiated here to speed up processing)
  private double _sina, _cosa, _a, _r, _re, _im, _rl;

  private static final String PARAM_C = "c";
  private static final String PARAM_POWER = "power";

  private static final String[] paramNames = {PARAM_C, PARAM_POWER};

  private double c = 0.1;
  private int power = 1;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    _c = c;
    if (power != 1) {
      _c /= ((double) power - 1);
    }
    _p2 = (double) power / 2.0;
    _vp = pAmount * (_c + 1);

    _a = atan2(pAffineTP.y, pAffineTP.x) * (double) power;
    _sina = sin(_a);
    _cosa = cos(_a);

    _r = _c * pow(sqr(pAffineTP.x) + sqr(pAffineTP.y), _p2);

    _re = _r * _cosa + 1;
    _im = _r * _sina;
    _rl = _vp / (sqr(_re) + sqr(_im));

    pVarTP.x += _rl * (pAffineTP.x * _re + pAffineTP.y * _im);
    pVarTP.y += _rl * (pAffineTP.y * _re - pAffineTP.x * _im);
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "murl";
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{c, power};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWER.equalsIgnoreCase(pName))
      power = (int) pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float murl_power = rintf(__murl_power);\n"
        + "float c = __murl_c;\n"
        + "if (murl_power != 1.f)\n"
        + "    c = __murl_c / (murl_power - 1.f);\n"
        + "float p2 = 0.5f*murl_power;\n"
        + "float vp = __murl*(__murl_c + 1.f);\n"
        + "float cosa;\n"
        + "float sina;\n"
        + "sincosf(atan2f(__y,__x)*murl_power ,&sina, &cosa);\n"
        + "float r = c*powf(__r2, p2);\n"
        + "float re = r*cosa + 1.f;\n"
        + "float im = r*sina;\n"
        + "float r1 = vp/(re*re+im*im);\n"
        + "\n"
        + "__px += r1 * (__x*re + __y*im);\n"
        + "__py += r1 * (__y*re - __x*im);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __murl*__z;\n" : "");
  }
}
