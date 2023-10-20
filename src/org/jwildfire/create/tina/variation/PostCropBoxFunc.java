package org.jwildfire.create.tina.variation;



import js.glsl.G;
import js.glsl.vec2;
import org.jwildfire.base.Tools;
import org.jwildfire.create.tina.base.XForm;
import org.jwildfire.create.tina.base.XYZPoint;

public class  PostCropBoxFunc  extends VariationFunc  implements SupportsGPU {

	/*
	 * Variation : post_crop_box
	 * Autor: Jesus Sosa
	 * Date: february 3, 2020
	 * Reference & Credits : https://www.shadertoy.com/view/llVyWW
	 */



	private static final long serialVersionUID = 1L;



	private static final String PARAM_WIDTH = "width";
	private static final String PARAM_HEIGHT = "height";
	private static final String PARAM_INVERT = "invert";



	double width=0.5;
	double height=0.5;
	private int invert = 0;

	double thick=0.001;

	private static final String[] additionalParamNames = { PARAM_WIDTH,PARAM_HEIGHT,PARAM_INVERT,};


	double sdBox( vec2 p, vec2 b )
	{
	    vec2 d = G.abs(p).minus(b);
	    return G.length(G.max(d,new vec2(0))) + G.min(G.max(d.x,d.y),0.0);
	}

	  public void transform(FlameTransformationContext pContext, XForm pXForm, XYZPoint pAffineTP, XYZPoint pVarTP, double pAmount) {
		  double x,y;
		  
	      x= pVarTP.x;
	      y =pVarTP.y;

  
			vec2 p=new vec2(x,y);

			double d=0.;

			// Box
			{
				// vec2 ra = G.cos( new vec2(0.0,1.57).plus(time)  ).multiply(0.3).plus(0.4);
				vec2 ra=new vec2(width,height);
			    d = sdBox( p, ra );
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
		return "post_crop_box";
	}

	public String[] getParameterNames() {
		return additionalParamNames;
	}


	public Object[] getParameterValues() { //re_min,re_max,im_min,im_max,
		return new Object[] { width,height,invert};
	}

	public void setParameter(String pName, double pValue) {
		if (pName.equalsIgnoreCase(PARAM_WIDTH)) {
			width = pValue;
		}
		else if (pName.equalsIgnoreCase(PARAM_HEIGHT)) {
			height = pValue;
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
	    return   "	  float x,y;"
	    		+""
	    		+"	  x= __px;"
	    		+"	  y =__py;"
	    		+"	  float2 p=make_float2(x,y);"
	    		+"	  float d=0.;"
	    		+""
	    		+"	  float2 ra=make_float2( __post_crop_box_width ,__post_crop_box_height);"
	    		+"	  d = post_crop_box_sdBox( p, ra );"
	    		+"    "
	    		+"	  __doHide=false;"
	    		+"	  if( __post_crop_box_invert ==0)"
	    		+"	   {"
	    		+"		  if (d>0.0)"
	    		+"		  { x=0.;"
	    		+"		    y=0.;"
	    		+"		    __doHide = true;"
	    		+"	       }"
	    		+"	  } else"
	    		+"	 {"
	    		+"	     if (d<=0.0 )"
	    		+"	     { x=0.;"
	    		+"	       y=0.;"
	    		+"	       __doHide = true;"
	    		+"	     }"
	    		+"	 }"
	    		+"		    __px = __post_crop_box * x;"
	    		+"		    __py = __post_crop_box * y;"
                + (context.isPreserveZCoordinate() ? "__pz += __post_crop_box * __z;\n" : "");
	  }
	  @Override
	  public String getGPUFunctions(FlameTransformationContext context) {
	    return  "__device__	float  post_crop_box_sdBox ( float2 p, float2 b )"
		       +"   {"
		       +"     float2 d = abs(p)-b;"
		       +"     return length(max(d,make_float2(0.0f,0.0f))) + fminf(fmaxf(d.x,d.y),0.0);"
		       +"}";
	  }		  
}


