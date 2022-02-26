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

public class Blade3DFunc extends SimpleVariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Z+ variation Jan 07
    procedure TXForm.Blade;
    var
      r, sinr, cosr: double;
    begin
      r := sqrt(sqr(FTx) + sqr(FTy))*vars[33];
      SinCos(r*random, sinr, cosr);
      FPx := FPx + vars[33] * FTx * (cosr + sinr);
      FPy := FPy + vars[33] * FTx * (cosr - sinr);
    end;
    */

    double r = pContext.random() * pAmount * sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);
    double sinr = sin(r);
    double cosr = cos(r);
    pVarTP.x += pAmount * pAffineTP.x * (cosr + sinr);
    pVarTP.y += pAmount * pAffineTP.x * (cosr - sinr);
    pVarTP.z += pAmount * pAffineTP.y * (sinr - cosr);
  }

  @Override
  public String getName() {
    return "blade3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float r = RANDFLOAT() * __blade3D * __r;\n"
        + "float cosr;\n"
        + "float sinr;\n"
        + "sincosf(r, &sinr, &cosr);\n"
        + "\n"
        + "__px += __blade3D * __x * (cosr + sinr);\n"
        + "__py += __blade3D * __x * (cosr - sinr);\n"
        + "__pz += __blade3D * __y * (sinr - cosr);\n";
  }
}
