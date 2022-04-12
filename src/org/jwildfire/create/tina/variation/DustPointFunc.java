package org.jwildfire.create.tina.variation;


import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

import static org.jwildfire.base.mathlib.MathLib.sqrt;

public class DustPointFunc extends SimpleVariationFunc {


  /**
   * Three Point Pivot/Overlap IFS Triangle
   *
   * @author Jesus Sosa
   * @date November 4, 2017
   * based on a work of Roger Bagula:
   * http://paulbourke.net/fractals/ifs_curved/roger5.c
   */


  private static final long serialVersionUID = 1L;


  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
//               based on a work of Roger Bagula:
//				 http://paulbourke.net/fractals/ifs_curved/roger5.c

    double x, y, p, r;

    p = (pContext.random() < 0.5) ? 1 : -1;
    r = sqrt(pAffineTP.x * pAffineTP.x + pAffineTP.y * pAffineTP.y);

    double w = pContext.random();
    if (w < 0.50) {
      x = pAffineTP.x / r - 1;
      y = p * pAffineTP.y / r;
    } else if (w < 0.75) {
      x = pAffineTP.x / 3.0;
      y = pAffineTP.y / 3.0;
    } else {
      x = pAffineTP.x / 3.0 + 2.0 / 3.0;
      y = pAffineTP.y / 3.0;
    }

    pVarTP.x += x * pAmount;
    pVarTP.y += y * pAmount;

    if (pContext.isPreserveZCoordinate()) {
      pVarTP.z += pAmount * pAffineTP.z;
    }

  }

  public String getName() {
    return "dustpoint";
  }

  @Override
  public VariationFuncType[] getVariationTypes() {
    return new VariationFuncType[]{VariationFuncType.VARTYPE_2D, VariationFuncType.VARTYPE_SIMULATION, VariationFuncType.VARTYPE_BASE_SHAPE, VariationFuncType.VARTYPE_SUPPORTED_BY_SWAN};
  }

}