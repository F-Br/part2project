package sideinfrastructure.genome;

public class GeneExpressionDetails {
    private Long releaseTime;
    private GeneExpressionType geneExpressionType;
    private Integer PID;
    private Boolean ownVarSupplied;
    private Integer optionalVarValue;

    public GeneExpressionDetails(Long releaseTime, GeneExpressionType geneExpressionType, Integer PID, Integer optionalVarValue) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.PID = PID;
        this.ownVarSupplied = true;
        this.optionalVarValue = optionalVarValue;
    }

    public GeneExpressionDetails(Long releaseTime, GeneExpressionType geneExpressionType, Integer PID) {
        this.releaseTime = releaseTime;
        this.geneExpressionType = geneExpressionType;
        this.PID = PID;
        this.ownVarSupplied = false;
    }


    public Long getReleaseTime() {
        return releaseTime;
    }

    public GeneExpressionType getGeneExpressionType() {
        return geneExpressionType;
    }

    public Integer getPID() {
        return PID;
    }

    public Boolean getOwnVarSupplied() {
        return ownVarSupplied;
    }

    public Integer getOptionalVarValue() {
        return optionalVarValue;
    }
}
