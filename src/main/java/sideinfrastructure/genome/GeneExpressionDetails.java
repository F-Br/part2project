package sideinfrastructure.genome;

import java.util.BitSet;

public class GeneExpressionDetails {
    private long releaseTime;
    private GeneExpressionType geneExpressionType;
    private int PID;
    private boolean ownVarSupplied;
    private BitSet optionalVarValue;

    public GeneExpressionDetails(long releaseTime, GeneExpressionType geneExpressionType, int PID, BitSet optionalVarValue) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.PID = PID;
        this.ownVarSupplied = true;
        this.optionalVarValue = optionalVarValue;
    }

    public GeneExpressionDetails(long releaseTime, GeneExpressionType geneExpressionType, int PID) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.PID = PID;
        this.ownVarSupplied = false;
    }


    public long getReleaseTime() {
        return releaseTime;
    }

    public GeneExpressionType getGeneExpressionType() {
        return geneExpressionType;
    }

    public int getPID() {
        return PID;
    }

    public boolean getOwnVarSupplied() {
        return ownVarSupplied;
    }

    public BitSet getOptionalVarValue() {
        return optionalVarValue;
    }
}
