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

public class Rays3Func extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // rays3 by Raykoid666, http://raykoid666.deviantart.com/art/re-pack-1-new-plugins-100092186 
    double t = sqr(pAffineTP.x) + sqr(pAffineTP.y);
    double u = 1.0 / sqrt(cos(sin(sqr(t) + SMALL_EPSILON) * sin(1.0 / sqr(t) + SMALL_EPSILON)));

    pVarTP.x = (pAmount / 10.0) * u * cos(t) * t / pAffineTP.x;
    pVarTP.y = (pAmount / 10.0) * u * tan(t) * t / pAffineTP.y;
    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }
  }

  @Override
  public String getName() {
    return "rays3";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float t = __x*__x + __y*__y;\n"
        + "    float u = 1.0f / sqrtf(cosf(sinf(t*t + 1.e-6f) * sinf(1.0f / t*t + 1.e-6f)));\n"
        + "\n"
        + "    __px = (__rays3 / 10.0f) * u * cosf(t) * t / __x;\n"
        + "    __py = (__rays3 / 10.0f) * u * tan(t) * t / __y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __rays3 * __z;\n" : "");
  }
}
