package com.sendsafely;

public class Progress implements ProgressInterface
{
    //The updateProgress method is a mandatory method that must be defined
    // by classes that implement ProgressInterface. This method can do
    // anything you want, or nothing at all.
    public void updateProgress(double progress)
    {
        System.out.println(progress + "%");
    }

    @Override
    public void updateProgress(String s, double v)
    {
        System.out.println("Uploading: " + s + " " + v + " : ");
    }

    @Override
    public void gotFileId(String s)
    {
        System.out.println("got file " + s);
    }
}