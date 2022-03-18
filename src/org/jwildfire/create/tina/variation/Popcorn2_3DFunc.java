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

public class Popcorn2_3DFunc extends VariationFunc implements SupportsGPU {
  private static final long serialVersionUID = 1L;

  private static final String PARAM_X = "x";
  private static final String PARAM_Y = "y";
  private static final String PARAM_Z = "z";
  private static final String PARAM_C = "c";
  private static final String[] paramNames = {PARAM_X, PARAM_Y, PARAM_Z, PARAM_C};

  private double x = 0.1;
  private double y = 0.1;
  private double z = 0.1;
  private double c = 3.0;

  @Override
  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
    /* popcorn2_3D by Larry Berlin, http://aporev.deviantart.com/art/3D-Plugins-Collection-One-138514007?q=gallery%3Aaporev%2F8229210&qo=15 */
    double inZ, otherZ, tempTZ, tempPZ, tmpVV;
    inZ = pAffineTP.z;
    otherZ = pVarTP.z;

    if (fabs(pAmount) <= 1.0) {
      tmpVV = fabs(pAmount) * pAmount; //sqr(pAmount) value retaining sign
    } else {
      tmpVV = pAmount;
    }
    if (otherZ == 0.0) {
      tempPZ = tmpVV * sin(tan(this.c)) * atan2(pAffineTP.y, pAffineTP.x);
    } else {
      tempPZ = pVarTP.z;
    }
    if (inZ == 0.0) {
      tempTZ = tmpVV * sin(tan(this.c)) * atan2(pAffineTP.y, pAffineTP.x);
    } else {
      tempTZ = pAffineTP.z;
    }

    pVarTP.x += pAmount * 0.5 * (pAffineTP.x + this.x * sin(tan(this.c * pAffineTP.y)));
    pVarTP.y += pAmount * 0.5 * (pAffineTP.y + this.y * sin(tan(this.c * pAffineTP.x)));
    pVarTP.z = tempPZ + tmpVV * (this.z * sin(tan(this.c)) * tempTZ);

    /*
        Original code:  
      pVarTP.x += pAmount * (pAffineTP.x + VAR(popcorn2_x) * sin(tan(VAR(popcorn2_c)*pAffineTP.y)));
      pVarTP.y += pAmount * (pAffineTP.y + VAR(popcorn2_y) * sin(tan(VAR(popcorn2_c)*pAffineTP.x)));
    */
  }

  @Override
  public String[] getParameterNames() {
    return paramNames;
  }

  @Override
  public Object[] getParameterValues() {
    return new Object[]{x, y, z, c};
  }

  @Override
  public void setParameter(String pName, double pValue) {
    if (PARAM_X.equalsIgnoreCase(pName))
      x = pValue;
    else if (PARAM_Y.equalsIgnoreCase(pName))
      y = pValue;
    else if (PARAM_Z.equalsIgnoreCase(pName))
      z = pValue;
    else if (PARAM_C.equalsIgnoreCase(pName))
      c = pValue;
    else
      throw new IllegalArgumentException(pName);
  }

  @Override
  public String getName() {
    return "popcorn2_3D";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_3D, VariationFuncType.VARTYPE_SUPPORTS_GPU, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

  @Override
  public String getGPUCode(FlameTransformationContext context) {
    // based on code from the cudaLibrary.xml compilation, created by Steven Brodhead Sr.
    return "float inZ, otherZ, tempTZ, tempPZ, tmpVV;\n"
        + "inZ    = __z;\n"
        + "otherZ = __pz;\n"
        + "\n"
        + "if (fabsf(__popcorn2_3D) <=1.f)\n"
        + "    tmpVV = fabsf(__popcorn2_3D)*__popcorn2_3D;\n"
        + "else\n"
        + "    tmpVV = __popcorn2_3D;\n"
        + "if(otherZ == 0.f)\n"
        + "    tempPZ = tmpVV*sinf(tan(__popcorn2_3D_c))*atan2f(__y,__x);\n"
        + "else\n"
        + "    tempPZ = __pz;\n"
        + "if(inZ == 0.f)\n"
        + "    tempTZ = tmpVV*sinf(tan(__popcorn2_3D_c))*atan2f(__y,__x);\n"
        + "else\n"
        + "    tempTZ = __z;\n"
        + "\n"
        + "__px += __popcorn2_3D * 0.5f * (__x + __popcorn2_3D_x * sinf(tan(__popcorn2_3D_c*__y)));\n"
        + "__py += __popcorn2_3D * 0.5f * (__y + __popcorn2_3D_y * sinf(tan(__popcorn2_3D_c*__x)));\n"
        + "__pz = tempPZ+tmpVV*(__popcorn2_3D_z*sinf(tan(__popcorn2_3D_c))*tempTZ);\n";
  }
}
