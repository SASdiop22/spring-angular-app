package edu.miage.springboot.web.dtos;

public class FolderDTO {

    private Long id;
    private String name;

    public FolderDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
