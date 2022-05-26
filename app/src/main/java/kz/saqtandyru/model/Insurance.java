package kz.saqtandyru.model;

import java.time.LocalDateTime;

public class Insurance {
    private String id;
    private String name;
    private String companyName;
    private LocalDateTime startDt;
    private LocalDateTime endDt;
    private InsuranceType insuranceType;
    private Long carId;
    private String carNumber;
    private String carName;

    public Insurance() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }


    public LocalDateTime getStartDt() {
        return startDt;
    }

    public void setStartDt(LocalDateTime startDt) {
        this.startDt = startDt;
    }

    public LocalDateTime getEndDt() {
        return endDt;
    }

    public void setEndDt(LocalDateTime endDt) {
        this.endDt = endDt;
    }
}
