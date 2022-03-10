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

import static org.jwildfire.base.mathlib.MathLib.log;
import static org.jwildfire.base.mathlib.MathLib.round;

public class Tile_LogFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_tile_log_spread = "spread";


  private static final String[] paramNames = {PARAM_tile_log_spread};

  private double spread = 1.0;

  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // tile_log by Zy0rg implemented into JWildfire by Brad Stefanov
    double x = -spread;
    if (pContext.random() < 0.5)
      x = spread;

    pVarTP.x += pAmount * (pAffineTP.x + round(x * log(pContext.random())));
    pVarTP.y += pAmount * pAffineTP.y;
    pVarTP.z += pAmount * pAffineTP.z;
  }


  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{spread};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_tile_log_spread.equalsIgnoreCase(pName))
      spread = pValue;

    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "tile_log";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "    float x = -__tile_log_spread;\n"
        + "    if (RANDFLOAT() < 0.5)\n"
        + "      x = __tile_log_spread;\n"
        + "\n"
        + "    __px += __tile_log * (__x + roundf(x * logf(RANDFLOAT())));\n"
        + "    __py += __tile_log * __y;\n"
        + "    __pz += __tile_log * __z;\n";
  }
}
