public class Pixel {
    public int alpha;
    public int red;
    public int green;
    public int blue;

    public Pixel (int rgb){
        this.alpha = getAlphaFromRGB(rgb);
        this.red = getRedFromRGB(rgb);
        this.green = getGreenFromRGB(rgb);
        this.blue = getBlueFromRGB(rgb);
    }

    public int getRGB(){
        // returns combined RGB channels into one int
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public void modifyLastBlueBit (int lastBit){
        // changes last bit in blue channel to either 0 or 1, specifed by int lastBit parameter
        if(lastBit == 0){
            blue = blue & 254;
        }
        else if (lastBit == 1){
            blue = (blue & 254) | 1;
        }
    }
    public void modifyLastRedBit (int lastBit){
        // changes last bit in red channel to either 0 or 1, specifed by int lastBit parameter
        if(lastBit == 0){
            red = red & 254;
        }
        else if (lastBit == 1){
            red = (red & 254) | 1;
        }
    }
    public void modifyLastGreenBit (int lastBit){
        // changes last bit in green channel to either 0 or 1, specifed by int lastBit parameter
        if(lastBit == 0){
            green = green & 254;
        }
        else if (lastBit == 1){
            green = (green & 254) | 1;
        }
    }
    public void modifyLastAlphaBit (int lastBit){
        // changes last bit in alpha channel to either 0 or 1, specifed by int lastBit parameter
        if(lastBit == 0){
            alpha = alpha & 254;
        }
        else if (lastBit == 1){
            alpha = (alpha & 254) | 1;
        }
    }

    public int getlastAlphaBit(){
        return alpha & 1;
    }
    public int getlastRedBit(){
        return red & 1;
    }
    public int getlastGreenBit(){
        return green & 1;
    }
    public int getlastBlueBit(){
        return blue & 1;
    }


    private static int getAlphaFromRGB (int rgb){
        //gets alpha channel from combined rgb int parameter
        return(rgb>>24) & 255;
    }
    private static int getRedFromRGB (int rgb){
        //gets red channel from combined rgb int parameter
        return(rgb>>16) & 255;
    }
    private static int getGreenFromRGB (int rgb){
        //gets green channel from combined rgb int parameter
        return(rgb>>8) & 255;
    }
    private static int getBlueFromRGB (int rgb){
        //get blue channel from combined rgb int parameter
        return rgb & 255;
    }

    public static void main (String[] args){

    }

    public String toString(){
        return "a:"+alpha+"\tr:"+red+"\tg:"+green+"\tb:"+blue;
    }
}


