package com.example.TaskApp.models;

public enum Role {
    MANAGER,
    DEVELOPER,
    REVIEWER;

//    public boolean canChangeState(Role role,TaskState from,TaskState to){
//
//        return switch (role){
//            case DEVELOPER -> (from == TaskState.BACKLOG && to == TaskState.INPROGRESS) ||
//                    (from == TaskState.INPROGRESS && to == TaskState.REVIEW);
//            case MANAGER -> from == TaskState.BACKLOG && to == TaskState.INPROGRESS;
//            case REVIEWER -> (from == TaskState.REVIEW && to == TaskState.DONE) ||
//                    (from == TaskState.REVIEW && to == TaskState.INPROGRESS);
//        };
//    }

}
