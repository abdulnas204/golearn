package com.makerloom.golearn.models;

import android.content.Context;
import android.text.TextUtils;

import com.makerloom.golearn.utils.QuestionsFilesUtils;

/**
 * Created by michael on 4/11/18.
 */

public class Course {
    private String courseCode;

    private String name;

    private Integer maxQuestions = 50;

    private Integer time = 30;

    private String courseDir;

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(Integer maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public Course (String courseCode, String name) {
        this(courseCode);
        this.name = name;
    }

    public Course (String courseCode, String name, String courseDir) {
        this(courseCode, name);
        this.courseDir = courseDir;
    }

    public Course (String courseCode) {
        this.courseCode = courseCode;
        this.name = this.courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourseDir() {
        return courseDir;
    }

    public Boolean hasCourseDir () {
        return !TextUtils.isEmpty(getCourseDir());
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public static Test generateTest (Course course, Context context, int questionLen) {
        return course.generateTest(context, questionLen);
    }

    public Test generateTest (Context context, int questionLen) {
        Questions questions = QuestionsFilesUtils.Companion.getQuestionsFile(context, getCourseCode());
        questions.prepare(questionLen);

        Test test = new Test();
        test.setCourse(Course.this);
        test.setDepartment(new Department());
        test.setQuestions(questions.getQuestions());

        return test;
    }
}
