/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emit.ipcv.utils;

/**
 * @author rinelfi
 */
public class Const {
	
	public static final int COLOR_MAX_LEVEL = 256;
	public static final int PIXEL_BACKGROUND = 255;
	public static final int PIXEL_FOREGROUND = 0;
	public static final int NEIGHBORHOOD = 3;
	public static final int NOTHING = -1;
	public static final String LEFT_DIRECTION = "LEFT";
	public static final String RIGHT_DIRECTION = "RIGHT";
	public static final String TOP_DIRECTION = "TOP";
	public static final String BOTTOM_DIRECTION = "BOTTOM";
	
	// Task
  public static final String PING = "ping";
	public static final String SHEAR = "shear";
	public static final String ROTATION = "rotation";
	public static final String HOMOTHETY = "homothety";
	public static final String VERTICAL_SYMMETRY = "vertical_symmetry";
	public static final String HORIZONTAL_SYMMETRY = "horizontal_symmetry";
	public static final String SYMMETRY_IN_THE_CENTER = "symmetry_in_the_center";
	public static final String COLOR_INVERSION = "color_inversion";
	public static final String BINARIZATION = "binarization";
	public static final String HISTOGRAM_EQUALIZATION = "histogram_equalization";
	public static final String DYNAMIC_DISPLAY = "dynamic_display";
	public static final String SOBEL_FILTER1 = "sobel_filter1";
	public static final String SOBEL_FILTER2 = "sobel_filter2";
	public static final String LAPLACIAN_FILTER = "laplacian_filter";
	public static final String REHAUSSEUR_FILTER = "rehausseur_filter";
	public static final String PERSONAL_FILTER1 = "personal_filter1";
	public static final String PERSONAL_FILTER2 = "personal_filter2";
	public static final String PERSONAL_FILTER3 = "personal_filter3";
	public static final String MEDIUM_FILTERING = "medium_filtering";
	public static final String GAUSSIAN_FILTERING = "gaussian_filtering";
	public static final String CONSERVATIVE_SMOOTHING = "conservative_smoothing";
	public static final String MEDIAN_FILTERING = "median_filtering";
	public static final String AVERAGE_FILTERING = "average_filtering";
	public static final String EROSION = "erosion";
	public static final String DILATION = "dilation";
	public static final String OPENING = "opening";
	public static final String ALL_OR_NOTHING = "all_or_nothing";
	public static final String THICKENING = "thickening";
	public static final String TOP_HAT_OPENING = "top_hat_opening";
	public static final String TOP_HAT_CLOSURE = "top_hat_closure";
	public static final String PAPERT_TURTLE = "papert_turtle";
	public static final String HISTOGRAM = "histogram";
	public static final String CUMULATIVE_HISTOGRAM = "cumulative_histogram";
}
