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

public class Waves22Func extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SCALEX = "scalex";
  private static final String PARAM_SCALEY = "scaley";
  private static final String PARAM_FREQX = "freqx";
  private static final String PARAM_FREQY = "freqy";
  private static final String PARAM_MODEX = "modex";
  private static final String PARAM_MODEY = "modey";
  private static final String PARAM_POWERX = "powerx";
  private static final String PARAM_POWERY = "powery";
  
  private static final String[] paramNames = {PARAM_SCALEX, PARAM_SCALEY, PARAM_FREQX, PARAM_FREQY, PARAM_MODEX, PARAM_MODEY, PARAM_POWERX, PARAM_POWERY};

  private double scalex = 0.05;
  private double scaley = 0.05;
  private double freqx = 7.0;
  private double freqy = 13.0;
  private int modex = 0;
  private int modey = 0;
  private double powerx = 2.0;
  private double powery = 2.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* weird waves22 from Tatyana Zabanova converted by Brad Stefanov https://www.deviantart.com/tatasz/art/Weird-Waves-Plugin-Pack-1-783560564*/
	double x0 = pAffineTP.x;
	double y0 = pAffineTP.y;
	double sinx;
	double siny;
	int px =  (int)powerx;
	int py =  (int)powery;	

	if (modex < 0.5){
        sinx = sin(y0 * freqx);
    } else {
        sinx = 0.5 * (1.0 + sin(y0 * freqx));
    }
	double offsetx = pow(sinx, px) * scalex;
	if (modey < 0.5){
        siny = sin(x0 * freqy);
    } else {
        siny = 0.5 * (1.0 + sin(x0 * freqy));
    }
    double offsety = pow(siny, py) * scaley;
    
    pVarTP.x += pAmount * (x0 + offsetx);
    pVarTP.y += pAmount * (y0 + offsety);
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
    return new Object[]{scalex, scaley, freqx, freqy, modex, modey, powerx, powery};
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
    else if (PARAM_MODEX.equalsIgnoreCase(pName))
      modex = (int) limitVal(pValue, 0, 1);
    else if (PARAM_MODEY.equalsIgnoreCase(pName))
      modey = (int) limitVal(pValue, 0, 1);
    else if (PARAM_POWERX.equalsIgnoreCase(pName))
      powerx = pValue;
    else if (PARAM_POWERY.equalsIgnoreCase(pName))
      powery = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "waves22";
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
        + "float sinx;\n"
        + "float siny;\n"
        + "int px = lroundf(__waves22_powerx);\n"
        + "int py = lroundf(__waves22_powery);\t\n"
        + "if (__waves22_modex < 0.5f){\n"
        + "  sinx = sinf(y0 * __waves22_freqx);\n"
        + "} else {\n"
        + "  sinx = 0.5f * (1.0f + sinf(y0 * __waves22_freqx));\n"
        + "}\n"
        + "float offsetx = (sinx < 0.f ? -1 : 1) * powf(fabsf(sinx), (float)px) * __waves22_scalex;\n"
        + "if (__waves22_modey < 0.5f){\n"
        + "  siny = sinf(x0 * __waves22_freqy);\n"
        + "} else {\n"
        + "  siny = 0.5f * (1.0f + sinf(x0 * __waves22_freqy));\n"
        + "}\n"
        + "float offsety = (siny < 0.f ? -1 : 1) * powf(fabsf(siny), (float)py) * __waves22_scaley;\n"
        + "__px += __waves22 * (x0 + offsetx);\n"
        + "__py += __waves22 * (y0 + offsety);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __waves22 * __z;\n" : "");
  }
}

