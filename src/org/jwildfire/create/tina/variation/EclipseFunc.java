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

public class EclipseFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_SHIFT = "shift";

  private static final String[] paramNames = {PARAM_SHIFT};

  private double shift = 0.10;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm,
                        XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /*
     * eclipse by Michael Faber,
     * http://michaelfaber.deviantart.com/art/Eclipse-268362046
     */

    if (fabs(pAffineTP.y) <= pAmount) {
      double c_2 = sqrt(sqr(pAmount)
              - sqr(pAffineTP.y));

      if (fabs(pAffineTP.x) <= c_2) {
        double x = pAffineTP.x + this.shift * pAmount;
        if (fabs(x) >= c_2) {
          pVarTP.x -= pAmount * pAffineTP.x;
        } else {
          pVarTP.x += pAmount * x;
        }
      } else {
        pVarTP.x += pAmount * pAffineTP.x;
      }
      pVarTP.y += pAmount * pAffineTP.y;
    } else {
      pVarTP.x += pAmount * pAffineTP.x;
      pVarTP.y += pAmount * pAffineTP.y;
    }
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
    return new Object[]{shift};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_SHIFT.equalsIgnoreCase(pName))
      shift = limitVal(pValue, -2.0, 2.0);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "eclipse";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float c_2;\n"
        + "float x;\n"
        + "if (fabsf(__y) <= __eclipse)\n"
        + "{\n"
        + "    c_2 = sqrtf( __eclipse*__eclipse - __y*__y);\n"
        + "    if ( fabsf(__x)  <= c_2)\n"
        + "    {\n"
        + "        x = __x + __eclipse_shift * __eclipse;\n"
        + "        if ( fabsf(x) >= c_2)\n"
        + "            __px -= __eclipse * __x;\n"
        + "        else\n"
        + "            __px += __eclipse * x;\n"
        + "    }\n"
        + "    else\n"
        + "    {\n"
        + "        __px += __eclipse * __x;\n"
        + "    }\n"
        + "    __py += __eclipse * __y;\n"
        + "}\n"
        + "else\n"
        + "{\n"
        + "    __px += __eclipse * __x;\n"
        + "    __py += __eclipse * __y;\n"
        + "}\n"
        + (context.isPreserveZCoordinate() ? "__pz += __eclipse * __z;\n" : "");
  }
}
