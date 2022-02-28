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

import static org.jwildfire.base.mathlib.MathLib.fabs;
import static org.jwildfire.base.mathlib.MathLib.pow;

public class LinearT3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_POWX = "powX";
  private static final String PARAM_POWY = "powY";
  private static final String PARAM_POWZ = "powZ";

  private static final String[] paramNames = {PARAM_POWX, PARAM_POWY, PARAM_POWZ};

  private double powX = 1.35;
  private double powY = 0.85;
  private double powZ = 1.15;

  private double sgn(double arg) {
    if (arg > 0)
      return 1.0;
    else
      return -1.0;
  }

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // linearT3D by FractalDesire, http://fractaldesire.deviantart.com/journal/linearT-plugin-219864320
    pVarTP.x += sgn(pAffineTP.x) * pow(fabs(pAffineTP.x), this.powX) * pAmount;
    pVarTP.y += sgn(pAffineTP.y) * pow(fabs(pAffineTP.y), this.powY) * pAmount;
    pVarTP.z += sgn(pAffineTP.z) * pow(fabs(pAffineTP.z), this.powZ) * pAmount;
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{powX, powY, powZ};
  }

  @Override
  public String[] getParameterAlternativeNames() {
    return new String[]{"lT_powX", "lT_powY", "lT_powZ"};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_POWX.equalsIgnoreCase(pName))
      powX = pValue;
    else if (PARAM_POWY.equalsIgnoreCase(pName))
      powY = pValue;
    else if (PARAM_POWZ.equalsIgnoreCase(pName))
      powZ = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "linearT3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "__px += (__x < 0.f ? -1.f : 1.f) * powf(fabsf(__x), __linearT3D_powX) * __linearT3D;\n"
         + "__py += (__y < 0.f ? -1.f : 1.f) * powf(fabsf(__y), __linearT3D_powY) * __linearT3D;\n"
         + "__pz += (__z < 0.f ? -1.f : 1.f) * powf(fabsf(__z), __linearT3D_powY) * __linearT3D;";
  }
}
