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

public class WedgeSphFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_ANGLE = "angle";
  private static final String PARAM_HOLE = "hole";
  private static final String PARAM_COUNT = "count";
  private static final String PARAM_SWIRL = "swirl";

  private static final String[] paramNames = {PARAM_ANGLE, PARAM_HOLE, PARAM_COUNT, PARAM_SWIRL};

  private double angle = 0.20;
  private double hole = 0.20;
  private double count = 2.0;
  private double swirl = 0.30;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* Wedge_sph from apo plugins pack */

    double r = 1.0 / (pAffineTP.getPrecalcSqrt() + SMALL_EPSILON);
    double a = pAffineTP.getPrecalcAtanYX() + swirl * r;
    double c = floor((count * a + M_PI) * M_1_PI * 0.5);

    double comp_fac = 1 - angle * count * M_1_PI * 0.5;

    a = a * comp_fac + c * angle;

    double sa = sin(a);
    double ca = cos(a);
    r = pAmount * (r + hole);

    pVarTP.x += r * ca;
    pVarTP.y += r * sa;
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
    return new Object[]{angle, hole, count, swirl};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_ANGLE.equalsIgnoreCase(pName))
      angle = pValue;
    else if (PARAM_HOLE.equalsIgnoreCase(pName))
      hole = pValue;
    else if (PARAM_COUNT.equalsIgnoreCase(pName))
      count = pValue;
    else if (PARAM_SWIRL.equalsIgnoreCase(pName))
      swirl = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "wedge_sph";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float rinv_eps = 1.f/sqrtf(__r2 ADD_EPSILON);\n"
        + "float a = __theta+__wedge_sph_swirl*rinv_eps;\n"
        + "float c = floorf((__wedge_sph_count*a+PI)*0.5f/PI);\n"
        + "float comp_fac = 1.f-__wedge_sph_angle*__wedge_sph_count*0.5f/PI;\n"
        + "a = a*comp_fac+c*__wedge_sph_angle;\n"
        + "__px += __wedge_sph*(rinv_eps+__wedge_sph_hole)*cosf(a);\n"
        + "__py += __wedge_sph*(rinv_eps+__wedge_sph_hole)*sinf(a);\n"
        + (context.isPreserveZCoordinate() ? "__pz += __wedge_sph*__z;\n" : "");
  }
}
