package tntm.graphics;

public class TntmDrawData {
    private static boolean beamBigger = true;
    public static float beamStroke = 0f;

    public static void renderer() {
        if(beamBigger) {
            beamStroke += 0.05f;

            if(beamStroke >= 2f) {
                beamStroke = 2f;
                beamBigger = false;
            }
        } else {
            beamStroke -= 0.05f;

            if(beamStroke <= 0f) {
                beamStroke = 0f;
                beamBigger = true;
            }
        }
    }
}