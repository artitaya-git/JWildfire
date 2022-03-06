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

import static org.jwildfire.base.mathlib.MathLib.cos;
import static org.jwildfire.base.mathlib.MathLib.sin;

public class PDJ3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_A = "a";
  private static final String PARAM_B = "b";
  private static final String PARAM_C = "c";
  private static final String PARAM_D = "d";
  private static final String PARAM_E = "e";
  private static final String PARAM_F = "f";  
  private static final String PARAM_G = "g";
  private static final String PARAM_H = "h";    
  private static final String[] paramNames = {PARAM_A, PARAM_B, PARAM_C, PARAM_D, PARAM_E, PARAM_F, PARAM_G, PARAM_H};

  private double a = 1;
  private double b = 2;
  private double c = 3;
  private double d = 4;
  private double e = 2;
  private double f = 0;
  private double g = 2;
  private double h = 0;  

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
   // 3D variables added by Brad Stefanov.     
    pVarTP.x += pAmount * (sin(a * pAffineTP.y) - cos(b * pAffineTP.x));
    pVarTP.y += pAmount * (sin(c * pAffineTP.x) - cos(d * pAffineTP.y));
    pVarTP.z += pAmount * (sin(e * pAffineTP.y) - cos(f * pAffineTP.z))*cos(g * pAffineTP.x) + sin(h * pAffineTP.z) ;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{a, b, c, d, e, f, g, h};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_A.equalsIgnoreCase(pName))
      a = pValue;
    else if (PARAM_B.equalsIgnoreCase(pName))
      b = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else if (PARAM_D.equalsIgnoreCase(pName))
      d = pValue;
    else if (PARAM_E.equalsIgnoreCase(pName))
      e = pValue;
    else if (PARAM_F.equalsIgnoreCase(pName))
      f = pValue;
    else if (PARAM_G.equalsIgnoreCase(pName))
      g = pValue;
    else if (PARAM_H.equalsIgnoreCase(pName))
      h = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "pdj3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "   __px += __pdj3D * (sinf(__pdj3D_a * __y) - cosf(__pdj3D_b * __x));\n"
        + "    __py += __pdj3D * (sinf(__pdj3D_c * __x) - cosf(__pdj3D_d * __y));\n"
        + "    __pz += __pdj3D * (sinf(__pdj3D_e * __y) - cosf(__pdj3D_f * __z))*cosf(__pdj3D_g * __x) + sinf(__pdj3D_h * __z) ;";
  }
}
