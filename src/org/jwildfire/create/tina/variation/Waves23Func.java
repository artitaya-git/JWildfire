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

public class Waves23Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;


  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY};

  private double scalex = 0.05;
  private double scaley = 0.05;
  private double freqx = 7;
  private double freqy = 13;



  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* waves23 from Tatyana Zabanova converted by Brad Stefanov https://www.deviantart.com/tatasz/art/Weird-Waves-Plugin-Pack-1-783560564*/
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double mx = y0 * freqx * M_1_2PI;
	double fx = mx - floor(mx);
	if (fx > 0.5) fx = 0.5 - fx;
	double my = x0 * freqy * M_1_2PI;
	double fy = my - floor(my);
	if (fy > 0.5) fy = 0.5 - fy;
    pVarTP.x += pAmount * (x0 + fx * scalex);
    pVarTP.y += pAmount * (y0 + fy * scaley);
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
    return new Object[]{scalex, scaley, freqx, freqy};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SCALEX.equalsIgnoreCase(pName))
      scalex = pValue;
    else if (PARAM_SCALEY.equalsIgnoreCase(pName))
      scaley = pValue;
    else if (PARAM_FREQX.equalsIgnoreCase(pName))
      freqx = pValue;
    else if (PARAM_FREQY.equalsIgnoreCase(pName))
      freqy = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves23";
  }
  
	@Override
	public boolean dynamicParameterExpansion() {
		return true;
	}

	@Override
	public boolean dynamicParameterExpansion(String pName) {
		// preset_id doesn't really expand parameters, but it changes them; this will make them refresh
		return true;
	}

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    return "float x0 = __x;\n"
        + "float y0 = __y;\n"
        + "float mx = y0 * __waves23_freqx / (PI + PI);\n"
        + "float fx = mx - floorf(mx);\n"
        + "if (fx > 0.5) fx = 0.5 - fx;\n"
        + "float my = x0 * __waves23_freqy / (PI + PI);\n"
        + "float fy = my - floorf(my);\n"
        + "if (fy > 0.5) fy = 0.5 - fy;\n"
        + "__px += __waves23 * (x0 + fx * __waves23_scalex);\n"
        + "__py += __waves23 * (y0 + fy * __waves23_scaley);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __waves23 * __z;\n" : "");
  }
}
