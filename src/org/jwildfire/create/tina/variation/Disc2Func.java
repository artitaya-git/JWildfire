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

import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.*;

public class Disc2Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ROT = "rot";
  private static final String PARAM_TWIST = "twist";

  private static final String[] paramNames = {PARAM_ROT, PARAM_TWIST};

  private double rot = 2.0;
  private double twist = 0.50;
  // precalculated
  private double timespi = 0.0;
  private double sinadd = 0.0;
  private double cosadd = 0.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07 */
    double t = timespi * (pAffineTP.x + pAffineTP.y);
    double sinr = sin(t);
    double cosr = cos(t);
    double r = pAmount * pAffineTP.getPrecalcAtan() / M_PI;

    pVarTP.x += (sinr + cosadd) * r;
    pVarTP.y += (cosr + sinadd) * r;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{rot, twist};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ROT.equalsIgnoreCase(pName))
      rot = pValue;
    else if (PARAM_TWIST.equalsIgnoreCase(pName))
      twist = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "disc2";
  }

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    double add = twist;
    timespi = rot * M_PI;
    sinadd = sin(add);
    cosadd = cos(add);
    cosadd -= 1;
    double k;
    if (add > 2.0 * M_PI) {
      k = (1 + add - 2.0 * M_PI);
      cosadd *= k;
      sinadd *= k;
    }
    if (add < -2.0 * M_PI) {
      k = (1 + add + 2.0 * M_PI);
      cosadd *= k;
      sinadd *= k;
    }
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float sinadd, cosadd;\n"
        + "sincosf(__disc2_twist, &sinadd, &cosadd);\n"
        + "cosadd -= 1.f;\n"
        + "if (fabsf(__disc2_twist)>2.f*PI)\n"
        + "{\n"
        + "    float sign = __disc2_twist >= 0.f ? 1.f : -1.f;\n"
        + "    float k = 1.f+ __disc2_twist-sign*2.f*PI;\n"
        + "    sinadd *= k;\n"
        + "    cosadd *= k;\n"
        + "}\n"
        + "float t = __disc2_rot*PI*(__x+__y);\n"
        + "__px += __disc2*__phi*(sinf(t)+cosadd)/PI;\n"
        + "__py += __disc2*__phi*(cosf(t)+sinadd)/PI;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __disc2*__z;\n" : "");
  }
}
