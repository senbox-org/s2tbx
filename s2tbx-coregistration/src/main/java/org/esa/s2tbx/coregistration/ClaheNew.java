package org.esa.s2tbx.coregistration;

import java.awt.*;

/**
 * Created by ramonag on 4/5/2017.
 */
public class ClaheNew {

    public void process(){
        float [ ] [ ] src = {   { 200, 18, 220, 200, 160 },
                { 180, 200, 180, 210, 200 },
                { 160, 180, 160, 200, 240 },
                {  250, 240, 22, 240, 250 }
        };
        //the size of the local region around a pixel for which the histogram is equalized. This size should be larger than the size of features to be preserved.
        int blockRadius = 12;//63;
        //the number of histogram bins used for histogram equalization. The implementation internally works with byte resolution, so values larger than 256 are not meaningful. This value also limits the quantification of the output when processing 8bit gray or 24bit RGB images. The number of histogram bins should be smaller than the number of pixels in a block.
        int bins = 2;//255;
        //limits the contrast stretch in the intensity transfer function. Very large values will let the histogram equalization do whatever it wants to do, that is result in maximal local contrast. The value 1 will result in the original image.
        float slope = 1;//3;
        int width = 5;
        int height = 4;
        float [ ] [ ] dst = {   { 0, 0, 0, 0, 100 },
            { 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0 },
            {  0, 0, 0, 0, 0 }
        };
        final java.awt.Rectangle roiBox = new Rectangle(5, 4);

//fin init

        Rectangle box;

        if ( roiBox == null )
        {
                box = new Rectangle( 0, 0, src.length, src[0].length );
        }
        else
            box = roiBox;

		/* make sure that the box is not larger than the image */
        box.width = Math.min( width - box.x, box.width );
        box.height = Math.min( height - box.y, box.height );

        final int boxXMax = box.x + box.height;
        final int boxYMax = box.y + box.width;

        for ( int y = box.y; y < boxYMax; y++ )
        {
            final int yMin = Math.max( 0, y - blockRadius );
            final int yMax = Math.min( height, y + blockRadius + 1 );
            final int h = yMax - yMin;

            final int xMin0 = Math.max( 0, box.x - blockRadius );
            final int xMax0 = Math.min( width - 1, box.x + blockRadius );

			/* initially fill histogram */
            final int[] hist = new int[ bins + 1 ];
            final int[] clippedHist = new int[ bins + 1 ];
            for ( int yi = yMin; yi < yMax; yi++ )
                for ( int xi = xMin0; xi < xMax0; ++xi )
                    ++hist[ roundPositive( src[xi][yi] / 255.0f * bins ) ];

            for ( int x = box.x; x < boxXMax; x++ )
            {
                final int v = roundPositive( src[x][y] / 255.0f * bins );

                final int xMin = Math.max( 0, x - blockRadius );
                final int xMax = x + blockRadius + 1;
                final int w = Math.min( width, xMax ) - xMin;
                final int n = h * w;

                final int limit;
                    limit = ( int )( slope * n / bins + 0.5f );

				/* remove left behind values from histogram */
                if ( xMin > 0 )
                {
                    final int xMin1 = xMin - 1;
                    for ( int yi = yMin; yi < yMax; ++yi )
                        --hist[ roundPositive( src[xMin1][yi] / 255.0f * bins ) ];
                }

				/* add newly included values to histogram */
                if ( xMax < width )
                {
                    final int xMax1 = xMax - 1;
                    for ( int yi = yMin; yi < yMax; ++yi )
                        ++hist[ roundPositive( src[xMax1][yi] / 255.0f * bins ) ];
                }

				/* clip histogram and redistribute clipped entries */
                System.arraycopy( hist, 0, clippedHist, 0, hist.length );
                int clippedEntries = 0, clippedEntriesBefore;
                do
                {
                    clippedEntriesBefore = clippedEntries;
                    clippedEntries = 0;
                    for ( int i = 0; i <= bins; i++ )
                    {
                        final int d = clippedHist[ i ] - limit;
                        if ( d > 0 )
                        {
                            clippedEntries += d;
                            clippedHist[ i ] = limit;
                        }
                    }

                    final int d = clippedEntries / ( bins + 1 );
                    final int m = clippedEntries % ( bins + 1 );
                    for ( int i = 0; i <= bins; ++i)
                        clippedHist[ i ] += d;

                    if ( m != 0 )
                    {
                        final int s = bins / m;
                        for ( int i = 0; i <= bins; i += s )
                            ++clippedHist[ i ];
                    }
                }
                while ( clippedEntries != clippedEntriesBefore );

				/* build cdf of clipped histogram */
                int hMin = bins;
                for ( int i = 0; i < hMin; ++i )
                    if ( clippedHist[ i ] != 0 ) hMin = i;

                int cdf = 0;
                for ( int i = hMin; i <= v; ++i )
                    cdf += clippedHist[ i ];

                int cdfMax = cdf;
                for ( int i = v + 1; i <= bins; ++i )
                    cdfMax += clippedHist[ i ];

                final int cdfMin = clippedHist[ hMin ];

                dst[x][y] = roundPositive( ( cdf - cdfMin ) / ( float )( cdfMax - cdfMin ) * 255.0f ) ;
            }

			/* multiply the current row into ip */
            final int t = y * width;
                final float min = getMin(src);
                for ( int x = box.x; x < boxXMax; ++x )
                {
                    final int i = t + x;
                    final float v = src[x][y];
                    final float a = ( float )dst[x][y] / src[x][y];
                    dst[x][y] = a * ( v - min ) + min ;
                }
        }
    }

    static private float getMin(float[][] source){
        float min = source[0][0];
        for(int i=0;i<source.length;i++){
            for(int j=0;j<source[i].length;j++){
                if(min > source[i][j]){
                    min = source[i][j];
                }
            }
        }
        return min;
    }

    private float getMax(float[][] source){
        float max = source[0][0];
        for(int i=0;i<source.length;i++){
            for(int j=0;j<source[i].length;j++){
                if(max < source[i][j]){
                    max = source[i][j];
                }
            }
        }
        return max;
    }

    final static private int roundPositive( float a )
    {
        return ( int )( a + 0.5f );
    }

}
