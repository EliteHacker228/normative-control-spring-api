package ru.maeasoftoworks.normativecontrol.api.jobpools;

import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class DocumentsVerificationPool {
    private final HashSet<String> jobPool;

    public DocumentsVerificationPool(){
        this.jobPool = new HashSet<>();
    }

    public boolean isVerificationInProgress(String documentId){
        return jobPool.contains(documentId);
    }

    public void finishVerification(String documentId){
        jobPool.remove(documentId);
    }

    public void startVerification(String documentId){
        jobPool.add(documentId);
    }
}
