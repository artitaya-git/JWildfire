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

public class Swirl3Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";

  private static final String[] paramNames = {PARAM_SHIFT};

  private double shift = 0.5;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    double rad = pAffineTP.getPrecalcSqrt();
    double ang = pAffineTP.getPrecalcAtanYX() + log(rad) * shift;
    double s = sin(ang);
    double c = cos(ang);

    pVarTP.x += pAmount * rad * c;
    pVarTP.y += pAmount * rad * s;

    if (pContext.isPreserveZCoordinate()) pVarTP.z += pAmount * pAffineTP.z;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{shift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "swirl3";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float rad = __r;\n"
        + "float ang = __theta + logf(rad) * __swirl3_shift;\n"
        + "float s = sinf(ang);\n"
        + "float c = cosf(ang);\n"
        + "    __px += __swirl3 * rad * c;\n"
        + "    __py += __swirl3 * rad * s;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __swirl3 * __z;\n": "");
  }
}
