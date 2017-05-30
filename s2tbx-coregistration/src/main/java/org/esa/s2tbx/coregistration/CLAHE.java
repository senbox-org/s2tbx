/*package org.esa.s2tbx.coregistration;

import org.opencv.core.*;

public class CLAHE {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args){
        CLAHE c = new CLAHE();
        Mat M = new Mat(7,7,CvType.CV_32FC2,new Scalar(1,3));
        //Mat source = new Mat();
        Mat N = c.apply(M);
    }

    private final static int BUFFER_SIZE = 256;
    private final double clipLimit_;
    private final int tilesX_;
    private final int tilesY_;
    Mat lut_ = new Mat();

    public CLAHE() {
        this(40, 8, 8);
    }

    public static int saturateCast(int x) {
        return x > BUFFER_SIZE - 1 ? BUFFER_SIZE - 1 : (x < 0 ? 0 : x);
    }

    public static int saturateCast(float x) {
        return (int) (x > BUFFER_SIZE - 1 ? BUFFER_SIZE - 1 : (x < 0 ? 0 : x));
    }

    public int GCD(int a, int b) {
        return b == 0 ? a : GCD(b, a % b);
    }

    public CLAHE(double clipLimit, int tilesX, int tilesY) {
        this.clipLimit_ = clipLimit;
        this.tilesX_ = tilesX;
        this.tilesY_ = tilesY;
    }

    public Mat apply(Mat src) {
        if (src.type() != CvType.CV_8UC1) {
            throw new IllegalArgumentException("Mat not of type CV_8UC1!");
        }
        Mat dst = new Mat(src.size(), src.type());
        lut_.create(tilesX_ * tilesY_, BUFFER_SIZE, CvType.CV_8UC1);

        Size tileSize;
        Mat srcForLut;

        if (src.cols() % tilesX_ == 0 && src.rows() % tilesY_ == 0) {
            tileSize = new Size(src.cols() / tilesX_, src.rows() / tilesY_);
            srcForLut = src;
        } else {
            Mat srcExt_ = new Mat();
            Core.copyMakeBorder(src, srcExt_, 0, tilesY_ - (src.rows() % tilesY_), 0, tilesX_ - (src.cols() % tilesX_), Core.BORDER_REFLECT_101);
            tileSize = new Size(srcExt_.cols() / tilesX_, srcExt_.rows() / tilesY_);
            srcForLut = srcExt_;
        }

        double tileSizeTotal = tileSize.area(); // int ?
        float lutScale = (float) ((BUFFER_SIZE - 1) / tileSizeTotal); // why BUFFER_SIZE - 1 ?
        int clipLimit = 0;
        if (clipLimit_ > 0.0) {
            clipLimit = (int) (clipLimit_ * tileSizeTotal / BUFFER_SIZE);
            if (clipLimit < 1) {
                clipLimit = 1;
            }
        }
        CLAHE_CalcLut_Body calcLutBody = new CLAHE_CalcLut_Body(srcForLut, lut_, tileSize, tilesX_, clipLimit, lutScale);
        calcLutBody.execute(new Range(0, tilesX_ * tilesY_));

        CLAHE_Interpolation_Body interpolationBody = new CLAHE_Interpolation_Body(src, dst, lut_, tileSize, tilesX_, tilesY_);
        interpolationBody.execute(new Range(0, src.rows()));

        return dst;
    }

    private class CLAHE_Interpolation_Body {

        Mat src_;
        Mat dst_;
        Mat lut_;
        Size tileSize_;
        int tilesX_;
        int tilesY_;

        CLAHE_Interpolation_Body(Mat src, Mat dst, Mat lut_, Size tileSize, int tilesX_, int tilesY_) {
            this.src_ = src;
            this.dst_ = dst;
            this.lut_ = lut_;
            this.tileSize_ = tileSize;
            this.tilesX_ = tilesX_;
            this.tilesY_ = tilesY_;
        }

        void execute(Range range) {
            int lut_step = (int) lut_.step1();
            int lut_break = tilesX_ * lut_step;

            for (int y = range.start; y < range.end; ++y) {

                float tyf = (y / (float) tileSize_.height) - 0.5f;
                int ty1 = (int) Math.floor(tyf);
                int ty2 = ty1 + 1;
                float ya = tyf - ty1;
                // keep largest
                if (ty1 < 0) {
                    ty1 = 0;
                }
                // keep smallest
                if (ty2 > tilesY_ - 1) {
                    ty2 = tilesY_ - 1;
                }

                int lutPlane1 = ty1 * tilesX_;
                int lutPlane2 = ty2 * tilesX_;

                for (int x = 0; x < src_.cols(); x++) {

                    float txf = (x / (float) tileSize_.width) - 0.5f;
                    int tx1 = (int) Math.floor(txf);
                    int tx2 = tx1 + 1;
                    float xa = txf - tx1;
                    // keep largest
                    if (tx1 < 0) {
                        tx1 = 0;
                    }
                    // keep smallest
                    if (tx2 > tilesX_ - 1) {
                        tx2 = tilesX_ - 1;
                    }
                    // original pixel value
                    double[] ptr = src_.get(y, x);
                    int srcVal = (int) ptr[0];

                    int ind1 = tx1 * lut_step + srcVal;
                    int ind2 = tx2 * lut_step + srcVal;

                    int column1 = (ind1 + (ty1 * lut_break)) % lut_step;
                    int row1 = (ind1 + (ty1 * lut_break)) / lut_step;

                    int column2 = (ind2 + (ty1 * lut_break)) % lut_step;
                    int row2 = (ind2 + (ty1 * lut_break)) / lut_step;

                    int column3 = (ind1 + (ty2 * lut_break)) % lut_step;
                    int row3 = (ind1 + (ty2 * lut_break)) / lut_step;

                    int column4 = (ind2 + (ty2 * lut_break)) % lut_step;
                    int row4 = (ind2 + (ty2 * lut_break)) / lut_step;

                    float res = 0;

                    double[] lut_ptr1 = lut_.get(row1, column1);
                    res += ((byte) lut_ptr1[0] & 0xFF) * ((1.0f - xa) * (1.0f - ya));

                    double[] lut_ptr2 = lut_.get(row2, column2);
                    res += ((byte) lut_ptr2[0] & 0xFF) * ((xa) * (1.0f - ya));

                    double[] lut_ptr3 = lut_.get(row3, column3);
                    res += ((byte) lut_ptr3[0] & 0xFF) * ((1.0f - xa) * (ya));

                    double[] lut_ptr4 = lut_.get(row4, column4);
                    res += ((byte) lut_ptr4[0] & 0xFF) * ((xa) * (ya));

                    dst_.put(y, x, saturateCast(res));
                }
            }
        }
    }

    private class CLAHE_CalcLut_Body {

        Mat src_;
        Mat lut_;
        Size tileSize_;
        int tilesX_;
        int clipLimit_;
        float lutScale_;

        CLAHE_CalcLut_Body(Mat srcForLut, Mat lut_, Size tileSize, int tilesX_, int clipLimit, float lutScale) {
            this.src_ = srcForLut;
            this.lut_ = lut_;
            this.tileSize_ = tileSize;
            this.tilesX_ = tilesX_;
            this.clipLimit_ = clipLimit;
            this.lutScale_ = lutScale;
        }

        void execute(Range range) {
            int[] tileHist;
            int[] lutBytes = new int[lut_.height() * lut_.width()];
            for (int k = range.start; k < range.end; ++k) {
                int ty = k / tilesX_;
                int tx = k % tilesX_;
                // retrieve tile submatrix
                Rect tileROI = new Rect();
                tileROI.x = (int) (tx * tileSize_.width);
                tileROI.y = (int) (ty * tileSize_.height);
                tileROI.width = (int) tileSize_.width;
                tileROI.height = (int) tileSize_.height;
                Mat tile = src_.submat(tileROI);
                // calc histogram
                tileHist = new int[BUFFER_SIZE];
                int height = tileROI.height;

                for (int h = height; h > 0; h--) {
                    int x;
                    double[] ptr;
                    for (int w = 0; w < tileROI.width; w++) {
                        ptr = tile.get(h - 1, w);
                        tileHist[(int) ptr[0]]++;
                    }
                }
                // clip histogram
                if (clipLimit_ > 0) {
                    // how many pixels were clipped
                    int clipped = 0;
                    for (int i = 0; i < BUFFER_SIZE; ++i) {
                        if (tileHist[i] > clipLimit_) {
                            clipped += tileHist[i] - clipLimit_;
                            tileHist[i] = clipLimit_;
                        }
                    }
                    // redistribute clipped pixels
                    int redistBatch = clipped / BUFFER_SIZE;
                    int residual = clipped - redistBatch * BUFFER_SIZE;
                    for (int i = 0; i < BUFFER_SIZE; ++i) {
                        tileHist[i] += redistBatch;
                    }
                    for (int i = 0; i < residual; ++i) {
                        tileHist[i]++;
                    }
                }
                // calc Lut
                int sum = 0;
                for (int i = 0; i < BUFFER_SIZE; ++i) {
                    sum += tileHist[i];
                    lut_.put(k, i, saturateCast(Math.round(sum * lutScale_)));
                }
            }
        }
    }
}

//  DISCLAIMER :

// This software is provided "as is".
// Any express or implied warranties, including, but not limited to, the implied
// warranties of merchantability and fitness for a particular purpose are disclaimed.
// In no event shall the blog writer be liable for any direct,
// indirect, incidental, special, exemplary, or consequential damages
// (including, but not limited to, procurement of substitute goods or services;
// loss of use, data, or profits; or business interruption) however caused
// and on any theory of liability, whether in contract, strict liability,
// or tort (including negligence or otherwise) arising in any way out of
// the use of this software, even if advised of the possibility of such damage.
*/