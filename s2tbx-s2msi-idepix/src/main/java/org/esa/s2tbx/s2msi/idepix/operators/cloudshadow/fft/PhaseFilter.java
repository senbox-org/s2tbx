package org.esa.s2tbx.s2msi.idepix.operators.cloudshadow.fft;

import org.jblas.ComplexDouble;
import org.jblas.ComplexDoubleMatrix;
import org.jblas.DoubleMatrix;

import static org.jblas.MatrixFunctions.powi;

public class PhaseFilter {

    // Original in s1tbx: PhaseFilter class for now only aggregates static methods for phase filtering of InSAR data
    // here: reduced to convolution

    //private static final double GOLDSTEIN_THRESHOLD = 1e-20;

    //private String method;
    private ComplexDoubleMatrix data;
    private int blockSize;
    private int overlap;
    private ComplexDoubleMatrix kernel2d_ring;
    private ComplexDoubleMatrix kernel2d_circle;

    private double[] spacing = new double[2];

    public PhaseFilter(ComplexDoubleMatrix data, int blockSize, int overlap, double kernelRadius, double kernelInnerRadius, double[] spacing) {
        this.data = data;
        this.blockSize = blockSize;
        this.overlap = overlap;
        setSpacing(spacing);

        this.kernel2d_ring = constructCircularKernel(kernelRadius, kernelInnerRadius);
        //this.kernel2d_circle = constructCircularKernel(kernelInnerRadius, 0.);
        this.kernel2d_circle = constructCircularKernel(kernelRadius, 0.);
    }

    //public void setMethod(String method) {
    //    this.method = method;
    //}

    public void setSpacing(double[] spacing){
        this.spacing[0] = spacing[0];
        this.spacing[1] = spacing[1];
    }

    public void setData(ComplexDoubleMatrix data) {
        this.data = data;
    }

    public ComplexDoubleMatrix getRingKernel(){
        return kernel2d_ring;
    }
    public ComplexDoubleMatrix getCircleKernel(){
        return kernel2d_circle;
    }
    public ComplexDoubleMatrix getData() {
        return data;
    }


    public ComplexDoubleMatrix convolutionSimple(ComplexDoubleMatrix filter) {

        // allocate output - same dimensions as input
        int totalY = data.rows;
        int totalX = data.columns;
        System.out.println(totalX);
        System.out.println(totalY);
        final ComplexDoubleMatrix outData = new ComplexDoubleMatrix(totalY, totalX);

        int blockSize = filter.columns;
        int center = (int) Math.ceil(blockSize/2.)-1;
        System.out.println(blockSize);
        System.out.println(center);


        // squared block assumed!
        // blockSize gives the length of the square filter side.
        // the position of the filter has to be iterated.
        // Only the central position receives a result.

        //Iteration over all central pixels, which can be at the center of the filter.
        for(int x = center; x < totalX - center; x++){    //column
            for(int y = center; y < totalY - center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize, blockSize);
                for(int i=0; i<blockSize; i++){ //column
                    for(int j=0; j<blockSize; j++){ //row
                        block.put(j, i, data.get(j+y-center, i+x-center));
                    }
                }

                // Convolution: get spectrum + filter + ifft
            //    SpectralUtils.fft2D_inplace(block);
                LinearAlgebraUtils.dotmult_inplace(block, filter); // the filter...
            //    SpectralUtils.invfft2D_inplace(block);

                //outData.put(y,x, block.get(center, center));
                outData.put(y,x, block.sum());
            }
        }

        return outData;
    }

    public DoubleMatrix convolutionSimpleGapFinder() {

        // filters have the same size (now)

        // allocate output - same dimensions as input
        int totalY = data.rows;
        int totalX = data.columns;
        //System.out.println(totalX);
        //System.out.println(totalY);
        final DoubleMatrix outData = new DoubleMatrix(totalY, totalX);

        int blockSize = kernel2d_circle.columns;
        int center = (int) Math.ceil(blockSize/2.)-1;
        //System.out.println(blockSize);
        //System.out.println(center);


        // squared block assumed!
        // blockSize gives the length of the square filter side.
        // the position of the filter has to be iterated.
        // Only the central position receives a result.

        // Convolution by hand...
        //Iteration over all central pixels, which can be at the center of the filter.


        for(int x = center; x < totalX - center; x++){    //column
            for(int y = center; y < totalY - center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize, blockSize);
                for(int i=0; i<blockSize; i++){ //column
                    for(int j=0; j<blockSize; j++){ //row
                        block.put(j, i, data.get(j+y-center, i+x-center));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize, blockSize);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, kernel2d_circle);
                LinearAlgebraUtils.dotmult_inplace(block2, kernel2d_ring);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }
        // Consider edges. Adjusting size of analysed matrix according to largest possible extent.
        // todo corners??
        // left side
        for(int x = center; x < totalX - center; x++){    //column
            for(int y = 0; y < center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(center+y, blockSize);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(center+y, blockSize);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(center+y, blockSize);

                for(int i=0; i<blockSize; i++){ //column
                    for(int j=0; j<center+y; j++){ //row
                        block.put(j, i, data.get(j, i+x-center));
                        filterCircle.put(j,i, kernel2d_circle.get(center-y+j,i));
                        filterRing.put(j,i, kernel2d_ring.get(center-y+j,i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(center+y, blockSize);
                block2.copy(block);


                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }
        // upper side
        for(int x = 0; x < center; x++){    //column
            for(int y = center; y < totalY-center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize, center+x);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(blockSize, center+x);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(blockSize, center+x);
                for(int i=0; i<center+x; i++){ //column
                    for(int j=0; j<blockSize; j++){ //row

                        block.put(j, i, data.get(j+y-center, i));
                        filterCircle.put(j,i, kernel2d_circle.get(j,center-x+i));
                        filterRing.put(j,i, kernel2d_ring.get(j,center-x+i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize, center+x);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }

        // right side
        for(int x = center; x < totalX - center; x++){    //column
            for(int y = totalY-center; y < totalY; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize);

                for(int i=0; i<blockSize; i++){ //column
                    for(int j=0; j<blockSize- (y - (totalY-center))-1; j++){ //row
                        block.put(j, i, data.get(y+j-center, i+x-center));
                        filterCircle.put(j,i, kernel2d_circle.get(j,i));
                        filterRing.put(j,i, kernel2d_ring.get(j,i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }

        //  bottom side
        for(int x = totalX-center; x < totalX ; x++){    //column
            for(int y = center; y < totalY-center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(blockSize, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(blockSize, blockSize- (x - (totalX-center))-1);

                for(int i=0; i<blockSize- (x - (totalX-center))-1; i++){ //column
                    for(int j=0; j<blockSize; j++){ //row
                        block.put(j, i, data.get(y+j-center, i+x-center));
                        filterCircle.put(j,i, kernel2d_circle.get(j,i));
                        filterRing.put(j,i, kernel2d_ring.get(j,i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize, blockSize- (x - (totalX-center))-1);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }

        // corners
        // upper left
        for(int x = 0; x < center; x++){    //column
            for(int y = 0; y < center; y++){ //row
                ComplexDoubleMatrix block = new ComplexDoubleMatrix(center+y, center+x);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(center+y, center+x);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(center+y, center+x);

                for(int i=0; i<center+x; i++){ //column
                    for(int j=0; j<center+y; j++){ //row
                        block.put(j, i, data.get(j, i));
                        filterCircle.put(j,i, kernel2d_circle.get(center-y+j,center-x+i));
                        filterRing.put(j,i, kernel2d_ring.get(center-y+j,center-x+i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(center+y, center+x);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }
        // upper right
        for(int x = 0; x < center ; x++){    //column
            for(int y = totalY-center; y < totalY; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, center+x);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, center+x);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, center+x);

                for(int i=0; i<center+x; i++){ //column
                    for(int j=0; j<blockSize- (y - (totalY-center))-1; j++){ //row
                        block.put(j, i, data.get(y+j-center, i));
                        filterCircle.put(j,i, kernel2d_circle.get(j,center-x+i));
                        filterRing.put(j,i, kernel2d_ring.get(j,center-x+i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, center+x);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }

        // lower left
        for(int x = totalX-center; x < totalX ; x++){
            for(int y = 0; y < center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(center+y, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(center+y, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(center+y, blockSize- (x - (totalX-center))-1);

                for(int i=0; i<blockSize- (x - (totalX-center))-1; i++){ //column
                    for(int j=0; j<center+y; j++){ //row
                        block.put(j, i, data.get(j, x+i-center));
                        filterCircle.put(j,i, kernel2d_circle.get(center-y+j,i));
                        filterRing.put(j,i, kernel2d_ring.get(center-y+j,i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(center+y, blockSize- (x - (totalX-center))-1);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }


        // lower right
        for(int x = totalX-center; x < totalX ; x++){    //column
            for(int y = totalY-center; y < totalY; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterRing = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize- (x - (totalX-center))-1);
                ComplexDoubleMatrix filterCircle = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize- (x - (totalX-center))-1);

                for(int i=0; i<blockSize- (x - (totalX-center))-1; i++){ //column
                    for(int j=0; j<blockSize- (y - (totalY-center))-1; j++){ //row
                        block.put(j, i, data.get(y+j-center, i+x-center));
                        filterCircle.put(j,i, kernel2d_circle.get(j,i));
                        filterRing.put(j,i, kernel2d_ring.get(j,i));
                    }
                }

                ComplexDoubleMatrix block2 = new ComplexDoubleMatrix(blockSize- (y - (totalY-center))-1, blockSize- (x - (totalX-center))-1);
                block2.copy(block);

                LinearAlgebraUtils.dotmult_inplace(block, filterCircle);
                LinearAlgebraUtils.dotmult_inplace(block2, filterRing);

                outData.put(y,x, block.sum().real() - block2.sum().real());
            }
        }

        return outData;
    }

    public ComplexDoubleMatrix convolutionSpecialTest(ComplexDoubleMatrix kernel) {

        // allocate output - same dimensions as input
        int totalY = data.rows;
        int totalX = data.columns;
        System.out.println(totalX);
        System.out.println(totalY);
        //final ComplexDoubleMatrix outData = new ComplexDoubleMatrix(totalY, totalX);

        int blockSize = kernel.columns;
        int center = (int) Math.ceil(blockSize/2.);

        ///
        // kernel gets extended, padded with zeros.
       // int thisDim = totalX;
       // if( totalY < totalX) thisDim = totalY;
        ComplexDoubleMatrix kernelData = new ComplexDoubleMatrix(totalY, totalX);
        for(int i=0; i<blockSize; i++){ //column
            for(int j=0; j<blockSize; j++){ //row
                kernelData.put(j, i, kernel.get(j,i));
            }
        }
        System.out.println(kernelData.rows);
        System.out.println(kernelData.columns);

        SpectralUtils.fft2D_inplace(kernelData);
        ComplexDoubleMatrix thisData = new ComplexDoubleMatrix(totalY, totalX);
        thisData.copy(data);

        System.out.println(thisData.rows);
        System.out.println(thisData.columns);
        System.out.println(kernelData.rows);
        System.out.println(kernelData.columns);

        // Convolution: get spectrum + filter + ifft
        SpectralUtils.fft2D_inplace(thisData);
        LinearAlgebraUtils.dotmult_inplace(thisData, kernelData); // the filter...
        SpectralUtils.invfft2D_inplace(thisData);

        return thisData;
    }

    public void printFilterCircle(){
        for(int i=0; i<kernel2d_circle.columns; i++){
            for(int j=0; j<kernel2d_circle.rows; j++){
                System.out.print(kernel2d_circle.get(j,i));
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println();
    }
    public void printFilterRing(){
        for(int i=0; i<kernel2d_ring.columns; i++){
            for(int j=0; j<kernel2d_ring.rows; j++){
                System.out.print(kernel2d_ring.get(j,i));
                System.out.print('\t');
            }
            System.out.println();
        }
        System.out.println();
    }

    public void convolutionSpecial2Filter(ComplexDoubleMatrix filterRing, ComplexDoubleMatrix filterCircle) {

        // allocate output - same dimensions as input
        int totalY = data.rows;
        int totalX = data.columns;
        System.out.println(totalX);
        System.out.println(totalY);
        final ComplexDoubleMatrix outData = new ComplexDoubleMatrix(totalY, totalX);

        int center = overlap;

        // squared block assumed!
        // blockSize gives the length of the square filter side.
        // the position of the filter has to be iterated.
        // Only the central position receives a result.

        //Iteration over all central pixels, which can be at the center of the filter.
        for(int x = center; x < totalX - center; x++){    //column
            for(int y = center; y < totalY - center; y++){ //row

                ComplexDoubleMatrix block = new ComplexDoubleMatrix(blockSize, blockSize);
                for(int i=0; i<blockSize; i++){ //column
                    for(int j=0; j<blockSize; j++){ //row
                        block.put(j, i, data.get(j+y-center, i+x-center));
                    }
                }
                ComplexDoubleMatrix block2 = block;

                // Convolution: get spectrum + filter + ifft
            //    SpectralUtils.fft2D_inplace(block);
            //    LinearAlgebraUtils.dotmult_inplace(block, filterRing); // the filter...
            //    SpectralUtils.invfft2D_inplace(block);

                SpectralUtils.fft2D_inplace(block2);
                LinearAlgebraUtils.dotmult_inplace(block2, filterCircle); // the filter...
                SpectralUtils.invfft2D_inplace(block2);

            //    outData.put(y,x, block.get(center, center).sub( block2.get(center, center)));
                outData.put(y,x, block2.get(center, center));
            }
        }

        data = outData;
    }


    public static DoubleMatrix arrangeKernel2d(DoubleMatrix kernel2dIn, final double scaleFactor) {

        final int kernelLines = kernel2dIn.rows;
        final int kernelPixels = kernel2dIn.columns;

        final int size = kernelLines;

        final int hbsL = (kernelLines / 2);
        final int hbsP = (kernelPixels / 2);
        final int extraL = isEven(kernelLines) ? 1 : 0; // 1 less to fill
        final int extraP = isEven(kernelPixels) ? 1 : 0; // 1 less to fill

        DoubleMatrix kernel2dOut = new DoubleMatrix(size, size); // allocate THE matrix
        int rowCnt = 0;
        int colCnt;

        for (int ii = -hbsL + extraL; ii <= hbsL; ++ii) {
            colCnt = 0;
            final int indexii = (ii + size) % size;
            for (int jj = -hbsP + extraP; jj <= hbsP; ++jj) {
                final int indexjj = (jj + size) % size;
                kernel2dOut.put(indexii, indexjj, kernel2dIn.get(rowCnt, colCnt));
                colCnt++;
            }
            rowCnt++;
        }

        if (scaleFactor != 1) {
            kernel2dOut.muli(scaleFactor);
        }

        return kernel2dOut;
    }

    public static boolean isEven(long value) {
        return value % 2 == 0;
    }



    /**
     * B = smooth(A,KERNEL)
     * (circular) spatial moving average with a (2N+1,2N+1) block.
     * See also matlab script smooth.m for some tests.
     * implementation as convolution with FFT's
     * input: KERNEL is the FFT of the kernel (block)
     */
    private DoubleMatrix smooth(final DoubleMatrix inData, final ComplexDoubleMatrix kernel2d) {
        ComplexDoubleMatrix outData = new ComplexDoubleMatrix(inData);      // or define fft(R4)
        SpectralUtils.fft2D_inplace(outData);                               // or define fft(R4)
        LinearAlgebraUtils.dotmult_inplace(outData, kernel2d.conj());
        SpectralUtils.invfft2D_inplace(outData);         // convolution, but still complex...
        return outData.real();                           // you know it is real only...
    }


    /**
     * B = smooth(A,blocksize)
     * (circular) spatial moving average with a (2N+1,2N+1) block.
     * See also matlab script smooth.m for some tests.
     */
    @Deprecated
   /* public static DoubleMatrix smoothSpace(final DoubleMatrix data, final int blockSize) {

        if (blockSize == 0)
            return data;

        final int nRows = data.rows;
        final int nCols = data.columns;
        final DoubleMatrix smoothData = new DoubleMatrix(nRows, nCols);

        double sum = 0.;
        double nSmooth = (2 * blockSize + 1) * (2 * blockSize + 1);
        int indexii;
        for (int i = 0; i < nRows; ++i) {
            for (int j = 0; j < nCols; ++j) {
                // Smooth this pixel
                for (int ii = -blockSize; ii <= blockSize; ++ii) {
                    indexii = (i + ii + nRows) % nRows;
                    for (int jj = -blockSize; jj <= blockSize; ++jj) {
                        sum += data.get(indexii, (j + jj + nCols) % nCols);
                    }
                }
                smoothData.put(i, j, sum / nSmooth);
                sum = 0.;
            }
        }
        return smoothData;
    }*/

    // Do the same as smoothSpace but faster   -----> for Goldstein filter ??????
    // some overhead due to conversion r4<->cr4
    private DoubleMatrix smoothSpectral(final DoubleMatrix data, final int blockSize) {

        final int nRows = data.rows;
        final int nCols = data.columns;
        final ComplexDoubleMatrix smoothData = new ComplexDoubleMatrix(nRows, nCols); // init to zero...

        SpectralUtils.fft2D_inplace(smoothData); // or define fft(R4)
        ComplexDoubleMatrix kernel = new ComplexDoubleMatrix(1, nRows); // init to zeros

        // design 1d kernel function of block
        for (int ii = -blockSize; ii <= blockSize; ++ii) {
            kernel.put(0, (ii + nRows) % nRows, new ComplexDouble(1.0 / (2 * blockSize + 1), 0.0));
        }

        ComplexDoubleMatrix kernel2d = LinearAlgebraUtils.matTxmat(kernel, kernel);
        SpectralUtils.fft2D_inplace(kernel2d); // should be real sinc
        LinearAlgebraUtils.dotmult(smoothData, kernel2d);
        SpectralUtils.invfft2D_inplace(smoothData);  // convolution, but still complex...
        return smoothData.real();
    }


    // use FFT's for convolution with smoothkernel
    // this could also be done static, or in the calling routine
    // KERNEL2D is FFT2 of even kernel (no imag part after fft!)
    /*private void constructSmoothingKernel() {

        ComplexDoubleMatrix kernel1D = new ComplexDoubleMatrix(1, blockSize);             // init to zeros

        int smooth = kernelArray.length / 2;

        for (int ii = -smooth; ii <= smooth; ++ii) {// 1d kernel function of block
            int tmpValue_1 = (ii + blockSize) % blockSize;
            int tmpValue_2 = ii + smooth;// used to be ii-SMOOTH: wrong
            kernel1D.put(0, tmpValue_1, new ComplexDouble(kernelArray[tmpValue_2]));
        }

        kernel2d = LinearAlgebraUtils.matTxmat(kernel1D, kernel1D);
        SpectralUtils.fft2D_inplace(kernel2d);  // should be real sinc
    }

    private void constructRectKernel() {

        // 1d kernel
        final DoubleMatrix kernel1d = new DoubleMatrix(1, blockSize); // init to zeros
        final int overlapLines = (int) Math.floor(kernelArray.length / 2.);

        // 1d kernel function
        for (int ii = -overlapLines; ii <= overlapLines; ++ii) {
            kernel1d.put(0, (ii + blockSize) % blockSize, kernelArray[ii + overlapLines]);
        }

        kernel2d = new ComplexDoubleMatrix(LinearAlgebraUtils.matTxmat(kernel1d, kernel1d));
        SpectralUtils.fft2D_inplace(kernel2d);
        kernel2d.conji();
    }*/

    private ComplexDoubleMatrix constructCircularKernel(double radius, double radius_inner){

        int[] nhkern = new int[2];
        for(int i=0; i<2; i++) nhkern[i] = (int) Math.ceil(radius/spacing[i]);

        int Nx = 2*nhkern[1]+1;
        int Ny = 2*nhkern[0]+1;
        double ydist;
        double xdist;

        double[][] kernel = new double[Ny][Nx];
        int N =0;

        for(int i=0; i<Ny; i++ ){
            for(int j=0; j<Nx; j++ ){
                ydist = (i-nhkern[0])*spacing[0];
                xdist = (j-nhkern[1])*spacing[1];

                if(radius > 0.){
                    if( Math.sqrt(Math.pow(ydist,2)+Math.pow(xdist,2)) < radius){
                        if(radius_inner >0.){
                            if( Math.sqrt(Math.pow(ydist,2)+Math.pow(xdist,2)) > radius_inner){
                                kernel[i][j] = 1.;
                                N++;
                            }
                        }
                        else{
                            kernel[i][j] = 1.;
                            N++;
                        }
                    }
                }
            }
        }

        if(N>0){
            for(int i=0; i<Ny; i++ ){
                for(int j=0; j<Nx; j++ ){
                    kernel[i][j] /= N;
                }
            }
        }

        ComplexDoubleMatrix kernel2d = new ComplexDoubleMatrix(kernel);
        //SpectralUtils.fft2D_inplace(kernel2d);
        return kernel2d;

    }
    private ComplexDoubleMatrix constructCircularKernel(double radius, double radius_inner, double kernelDefRadius){

        int[] nhkern = new int[2];
        for(int i=0; i<2; i++) nhkern[i] = (int) Math.ceil(kernelDefRadius/spacing[i]);

        int Nx = 2*nhkern[1]+1;
        int Ny = 2*nhkern[0]+1;
        double ydist;
        double xdist;

        double[][] kernel = new double[Ny][Nx];
        int N =0;

        for(int i=0; i<Ny; i++ ){
            for(int j=0; j<Nx; j++ ){
                ydist = (i-nhkern[0])*spacing[0];
                xdist = (j-nhkern[1])*spacing[1];

                if(radius > 0.){
                    if( Math.sqrt(Math.pow(ydist,2)+Math.pow(xdist,2)) < radius){
                        if(radius_inner >0.){
                            if( Math.sqrt(Math.pow(ydist,2)+Math.pow(xdist,2)) > radius_inner){
                                kernel[i][j] = 1.;
                                N++;
                            }
                        }
                        else{
                            kernel[i][j] = 1.;
                            N++;
                        }
                    }
                }
            }
        }

        if(N>0){
            for(int i=0; i<Ny; i++ ){
                for(int j=0; j<Nx; j++ ){
                    kernel[i][j] /= N;
                }
            }
        }

        ComplexDoubleMatrix kernel2d = new ComplexDoubleMatrix(kernel);
        SpectralUtils.fft2D_inplace(kernel2d);
        return kernel2d;

    }

    private void constructKernel() {
    //    constructRectKernel();

//        if (method.contains("goldstein")) {
//            constructSmoothingKernel();
//        } else if (method.contains("convolution")) {
//            constructRectKernel();
//        }
    }

}