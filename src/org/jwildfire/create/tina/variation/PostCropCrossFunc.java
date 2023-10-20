package org.jwildfire.create.tina.variation;

import static org.jwildfire.base.mathlib.MathLib.sin;

import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  PostCropCrossFunc  extends VariationFunc implements SupportsGPU {

	/*
	 * Variation : crop_cross
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;


	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_SIZE = "Size";
	private static final String PARAM_ROUND= "round";
	private static final String PARAM_INVERT = "invert";


	double width=0.5;
	double Size=0.5;
	double thick=0.001;
	private int invert = 0;




	private static final String[] additionalParamNames = { PARAM_WIDTH,PARAM_SIZE,PARAM_ROUND,PARAM_INVERT};


double sdCross( vec2 p,  vec2 b, double r ) 
{
    p = G.abs(p);
    p = (p.y>p.x) ? new vec2(p.y,p.x) : new vec2(p.x,p.y);
    
	vec2  q = p.minus(b);
    double k = G.max(q.y,q.x);
    vec2  w = (k>0.0) ? q : new vec2(b.y-p.x,-k);
    
    return G.sign(k)*G.length(G.max(w,0.0)) + r;
}



	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

 // Cross
			{
				//vec2 si = G.cos( new vec2(0.0,1.57).plus(time) ).multiply(0.3).plus(0.8); 	    
				vec2 si=new vec2(width,Size);
				if( si.x<si.y ) 
				   	si=new vec2(si.y,si.x);
				    // corner radious
				 double ra = 0.1*sin(thick*1.2);
				 d = sdCross( p, si, ra );
			}
	
			


	    
		    pVarTP.doHide=false;
		    if(invert==0)
		    {
		      if (d>0.0)
		      { x=0.;
		        y=0.;
		        pVarTP.doHide = true;
		      }
		    } else
		    {
			      if (d<=0.0 )
			      { x=0.;
			        y=0.;
			        pVarTP.doHide = true;
			      }
		    }
		    pVarTP.x = pAmount * x;
		    pVarTP.y = pAmount * y;
		    if (pContext.isPreserveZCoordinate()) {
		      pVarTP.z += pAmount * pAffineTP.z;
		    }
		  }

	public String getName() {
		return "post_crop_cross";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { width,Size,thick,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_SIZE)) {
			Size = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_ROUND)) {
			thick = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_INVERT)) {
			   invert =   (int)Tools.limitValue(pValue, 0 , 1);
		}
		else
		      throw new IllegalArgumentException(pName);
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
	  public int getPriority() {
	    return 1;
	  }

	@Override
	public VariationFuncType[] getVariationTypes() {
		return new VariationFuncType[]{VariationFuncType.VARTYPE_CROP, VariationFuncType.VARTYPE_POST, VariationFuncType.VARTYPE_SUPPORTS_GPU};
	}
	  @Override
	  public String getGPUCode(FlameTransformationContext context) {
	    return   "		  float x,y;"
	    		+"		  "
	    		+"	      x= __px;"
	    		+"	      y =__py;"
	    		+"			float2 p=make_float2(x,y);"
	    		+"			float d=0.;"
	    		+" "
	    		+"			{"
	    		+"				"
	    		+"				float2 si=make_float2( __post_crop_cross_width , __post_crop_cross_Size);"
	    		+"				if( si.x<si.y ) "
	    		+"				   	si=make_float2(si.y,si.x);"
	    		+"				    "
	    		+"				 float ra = 0.1*sinf(__post_crop_cross_round*1.2);"
	    		+"				 d = post_crop_cross_sdCross( p, si, ra );"
	    		+"			}"
	    		+"		    __doHide=false;"
	    		+"		    if( __post_crop_cross_invert ==0)"
	    		+"		    {"
	    		+"		      if (d>0.0)"
	    		+"		      { x=0.;"
	    		+"		        y=0.;"
	    		+"		        __doHide = true;"
	    		+"		      }"
	    		+"		    } else"
	    		+"		    {"
	    		+"			      if (d<=0.0 )"
	    		+"			      { x=0.;"
	    		+"			        y=0.;"
	    		+"			        __doHide = true;"
	    		+"			      }"
	    		+"		    }"
	    		+"		    __px = __post_crop_cross * x;"
	    		+"		    __py = __post_crop_cross * y;"
                + (context.isPreserveZCoordinate() ? "__pz += __post_crop_cross * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__	float post_crop_cross_sdCross( float2 p,  float2 b, float r ) "
	    		+"{"
	    		+"    p = abs(p);"
	    		+"    p = (p.y>p.x) ? make_float2(p.y,p.x) : make_float2(p.x,p.y);"
	    		+"    "
	    		+"	float2  q = p-(b);"
	    		+"    float k = fmaxf(q.y,q.x);"
	    		+"    float2  w = (k>0.0) ? q : make_float2(b.y-p.x,-k);"
	    		+"    "
	    		+"    return sign(k)*length(max(w,0.0f)) + r;"
	    		+"}";
	  }	
}


