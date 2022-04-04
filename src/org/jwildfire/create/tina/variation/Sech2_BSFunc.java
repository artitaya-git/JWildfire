/*
  JWildfire - an image and animation processor written in Java 
  Copyright (C) 1995-2011 Andreas Maschke

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

public class Sech2_BSFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X1 = "x1";
  private static final String PARAM_X2 = "x2";
  private static final String PARAM_Y1 = "y1";
  private static final String PARAM_Y2 = "y2";
  private static final String[] paramNames = {PARAM_X1, PARAM_X2, PARAM_Y1, PARAM_Y2};
  private double x1 = 1.25;
  private double x2 = 0.75;
  private double y1 = 1.5;
  private double y2 = 0.75;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* complex vars by cothe */
    /* exp log sin cos tan sec csc cot sinh cosh tanh sech csch coth */
    /* Variables added by Brad Stefanov */
    //Hyperbolic Secant SECH
    double sechsin = sin(pAffineTP.y * y1);
    double sechcos = cos(pAffineTP.y * y2);
    double sechsinh = sinh(pAffineTP.x * x1);
    double sechcosh = cosh(pAffineTP.x * x2);
    double d = (cos(2.0 * pAffineTP.y) + cosh(2.0 * pAffineTP.x));
    if (d == 0) {
      return;
    }
    double sechden = 2.0 / d;
    pVarTP.x += pAmount * sechden * sechcos * sechcosh;
    pVarTP.y -= pAmount * sechden * sechsin * sechsinh;
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
    return new Object[]{x1, x2, y1, y2};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X1.equalsIgnoreCase(pName))
      x1 = pValue;
    else if (PARAM_X2.equalsIgnoreCase(pName))
      x2 = pValue;
    else if (PARAM_Y1.equalsIgnoreCase(pName))
      y1 = pValue;
    else if (PARAM_Y2.equalsIgnoreCase(pName))
      y2 = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "sech2_bs";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D,VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }
  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return   "    float sechsin = sinf(__y *  __sech2_bs_y1 );"
    		+"    float sechcos = cosf(__y *  __sech2_bs_y2 );"
    		+"    float sechsinh = sinhf(__x *  __sech2_bs_x1 );"
    		+"    float sechcosh = coshf(__x *  __sech2_bs_x2 );"
    		+"    float d = (cosf(2.0 * __y) + coshf(2.0 * __x));"
    		+"    if (d != 0) {"
    		+"      float sechden = 2.0 / d;"
    		+"      __px += __sech2_bs * sechden * sechcos * sechcosh;"
    		+"      __py -= __sech2_bs * sechden * sechsin * sechsinh;"
    		+"    }"
            + (context.isPreserveZCoordinate() ? "__pz += __sech2_bs *__z;" : "");
  }
}
