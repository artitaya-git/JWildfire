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

import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.Layer;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.max;
import static org.jwildfire.base.mathlib.MathLib.min;

public class CropFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_LEFT = "left";
  private static final String PARAM_RIGHT = "right";
  private static final String PARAM_TOP = "top";
  private static final String PARAM_BOTTOM = "bottom";
  private static final String PARAM_SCATTER_AREA = "scatter_area";
  private static final String PARAM_ZERO = "zero";

  private static final String[] paramNames = {PARAM_LEFT, PARAM_RIGHT, PARAM_TOP, PARAM_BOTTOM, PARAM_SCATTER_AREA, PARAM_ZERO};

  private double left = -1.0;
  private double top = -1.0;
  private double right = 1.0;
  private double bottom = 1.0;
  private double scatter_area = 0.0;
  private int zero = 0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    // crop by Xyrus02, http://xyrus02.deviantart.com/art/Crop-Plugin-Updated-169958881
    double x = pAffineTP.x;
    double y = pAffineTP.y;
    if (((x < xmin) || (x > xmax) || (y < ymin) || (y > ymax)) && (zero != 0)) {
      pVarTP.x = pVarTP.y = 0;
      pVarTP.doHide = true;
      return;
    } else {
      pVarTP.doHide = false;
      if (x < xmin)
        x = xmin + pContext.random() * w;
      else if (x > xmax)
        x = xmax - pContext.random() * w;
      if (y < ymin)
        y = ymin + pContext.random() * h;
      else if (y > ymax)
        y = ymax - pContext.random() * h;
    }
    pVarTP.x = pAmount * x;
    pVarTP.y = pAmount * y;
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
    return new Object[]{left, right, top, bottom, scatter_area, zero};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_LEFT.equalsIgnoreCase(pName))
      left = pValue;
    else if (PARAM_RIGHT.equalsIgnoreCase(pName))
      right = pValue;
    else if (PARAM_TOP.equalsIgnoreCase(pName))
      top = pValue;
    else if (PARAM_BOTTOM.equalsIgnoreCase(pName))
      bottom = pValue;
    else if (PARAM_SCATTER_AREA.equalsIgnoreCase(pName))
      scatter_area = limitVal(pValue, -1.0, 1.0);
    else if (PARAM_ZERO.equalsIgnoreCase(pName))
      zero = limitIntVal(Tools.FTOI(pValue), 0, 1);
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "crop";
  }

  private double xmin, xmax, ymin, ymax, w, h;

  @Override
  public void init(FlameTransformationContext pContext, Layer pLayer, XForm pXForm, double pAmount) {
    xmin = min(left, right);
    ymin = min(top, bottom);
    xmax = max(left, right);
    ymax = max(top, bottom);
    w = (xmax - xmin) * 0.5 * scatter_area;
    h = (ymax - ymin) * 0.5 * scatter_area;
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float xmin, xmax, ymin, ymax, w, h;\n"
        + "xmin = fminf(__crop_left, __crop_right);\n"
        + "ymin = fminf(__crop_top, __crop_bottom);\n"
        + "xmax = fmaxf(__crop_left, __crop_right);\n"
        + "ymax = fmaxf(__crop_top, __crop_bottom);\n"
        + "w = (xmax - xmin) * 0.5f * __crop_scatter_area;\n"
        + "h = (ymax - ymin) * 0.5f * __crop_scatter_area;\n"
        + "float x = __x;\n"
        + "float y = __y;\n"
        + "if (((x < xmin) || (x > xmax) || (y < ymin) || (y > ymax)) && (lroundf(__crop_zero) != 0)) {\n"
        + "  __px = __py = 0;\n"
        + "  __doHide = true;\n"
        + "} else {\n"
        + "   __doHide = false;\n"
        + "   if (x < xmin)\n"
        + "     x = xmin + RANDFLOAT() * w;\n"
        + "   else if (x > xmax)\n"
        + "     x = xmax - RANDFLOAT() * w;\n"
        + "   if (y < ymin)\n"
        + "     y = ymin + RANDFLOAT() * h;\n"
        + "   else if (y > ymax)\n"
        + "     y = ymax - RANDFLOAT() * h;\n"
        + "  __px = __crop * x;\n"
        + "  __py = __crop * y;\n"
        + (context.isPreserveZCoordinate() ? "__pz += __crop*__z;\n" : "")
        + "}\n";
  }
}
