package sideinfrastructure.genome;

import java.util.BitSet;

public class GeneExpressionDetails {
    private long releaseTime;
    private GeneExpressionType geneExpressionType;
    private int validPID;
    private boolean ownVarSupplied;
    private BitSet optionalVarValue;

    public GeneExpressionDetails(long releaseTime, GeneExpressionType geneExpressionType, int validPID, BitSet optionalVarValue) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.validPID = validPID;
        this.ownVarSupplied = true;
        this.optionalVarValue = optionalVarValue;
    }

    public GeneExpressionDetails(long releaseTime, GeneExpressionType geneExpressionType, int validPID) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.validPID = validPID;
        this.ownVarSupplied = false;
    }


    public long getReleaseTime() {
        return releaseTime;
    }

    public GeneExpressionType getGeneExpressionType() {
        return geneExpressionType;
    }

    public int getPID() {
        return validPID;
    }

    public boolean getOwnVarSupplied() {
        return ownVarSupplied;
    }

    public BitSet getOptionalVarValue() {
        return optionalVarValue;
    }
}
