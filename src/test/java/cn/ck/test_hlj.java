package cn.ck;

import cn.ck.controller.jobs.getAlluser;
import cn.ck.entity.Jobs;
import cn.ck.entity.Studio;
import cn.ck.entity.bean.JobsStudio;
import cn.ck.service.JobsService;
import cn.ck.service.StudioService;
import cn.ck.service.impl.JobsServiceImpl;
import cn.ck.utils.ResponseBo;
import cn.ck.utils.utils_hlj.fartime;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.ibatis.session.SqlSession;
import org.junit.Before;
import cn.ck.utils.MyBatisGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class test_hlj {
    private SqlSession sqlSession;

    @Autowired
    private JobsService jobsService;
    @Autowired
    private StudioService studioService;

    @Test
    public void test() throws ParseException {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateformat.parse("2016-6-19");
        System.out.println(dateformat.format(date));
    }

    @Test
    public void testInsert() {
        Date date = new Date();
        System.out.println(date);
        Jobs jobs = new Jobs();
        jobs.setJobCreattime(date);
        jobs.setJobMoney("4k");
        jobs.setJobName("java工程师");
        jobs.setJobStudio("13");
        jobs.setJobIntro("java后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师后端工程师");
        jobs.setJobRequire("java后端工程师简介简介后端工程师简介简介后端工程师简介简介后端工程师简介简介后端工程师简介简介后端工程师简介简介后端工程师简介简介");
        jobs.setJobState("招聘中。。");
        jobs.setJobNum(3);
        jobs.setJobType("全职");

        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
        jobsService.insertAllColumn(jobs);
    }

    @Test
    public void testConcend() throws ParseException {
        //        生成Date类型数据
//        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = dateformat.parse("2016-6-19");
//        System.out.println(dateformat.format(date));
        Date date = new Date();
        System.out.println(date);

        Jobs jobs = new Jobs();

        jobs.setJobId(321);
        jobs.setJobCreattime(date);
        jobs.setJobMoney("1231");
        jobs.setJobName("999");
        jobs.setJobStudio("321");
        jobs.setJobIntro("4564564");
        jobs.setJobRequire("1231231");
        jobs.setJobState("4123");
        jobs.setJobNum(12);
        jobs.setJobType("45");
        jobsService.updateAllColumnById(jobs);
    }

    @Test
    public void testThird() {
        Jobs jobs = new Jobs();
//        List<Jobs> list = new ArrayList<Jobs>();//创建集合对象；
        jobs = jobsService.selectById(3212);
        System.out.println(jobs);
    }

    //    根据job id 删除
    @Test
    public void testdelect() {
        Date date = new Date();
        System.out.println(date);
        Jobs jobs = new Jobs();
        jobs.setJobId(333);
        boolean a = jobsService.delete(new EntityWrapper<Jobs>().eq("job_id", jobs.getJobId()));
    }

    @Test
    public void testSelectCount() {
        int jNum = jobsService.selectCount(new EntityWrapper<Jobs>());
        System.out.println("=========jNum " + jNum);
    }

    @Test
    public void test2() {
        //        获得所有的招聘信息
        List<Jobs> jobs = jobsService.selectList(new EntityWrapper<Jobs>());
        System.out.println("===========all jobs " + jobs);
        List<JobsStudio> jobsStudios = new ArrayList<>();


        for (Jobs jobs1 : jobs) {
            Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_id", jobs1.getJobStudio()));
            JobsStudio jobsStudio = new JobsStudio();
            jobsStudio.setJobs(jobs1);
            jobsStudio.setStudio(studio);
            jobsStudios.add(jobsStudio);
        }

        System.out.println("==== " + jobsStudios);
//        return ResponseBo.ok().put("jobsStudios",jobsStudios);
    }

    @Test
    public void selectStuName() {
        Studio studio = studioService.selectOne(new EntityWrapper<Studio>().eq("stu_name", "45"));
        if (studio == null) {
            System.out.println("空");
        } else {
            System.out.println("非空");
        }
    }

    @Test
    public void ft() {
//        Date date = new Date(118,9,1,12,1,1);      //2018.10.1  12:01:01
        Date date = new Date(117, 9, 1, 12, 1, 1);

//        System.out.println("========== "+date);
        Date d = new Date();

        long a = d.getTime() - date.getTime();
        long b = d.getTime() - 31536000;
        System.out.println("=======bb time: " + b);
//        System.out.println("========tiem: "+a);
//        System.out.println("=========="+d);
        fartime f = new fartime();
//        System.out.println("----------"+f.far(date));
    }

    @Test
    public void aa() {
        getAlluser g = new getAlluser();
        g.aa();
    }

    @Test
    public void bb() {

//        Calendar cld = Calendar.getInstance();
////
//        int year = cld.get(Calendar.YEAR);
//        System.out.println("年份：" + year);
//        System.out.println("月份：" + (cld.get(Calendar.MONTH) + 1));
//        System.out.println("日：" + cld.get(Calendar.DAY_OF_MONTH));

        Date date = new Date(117, 9, 1, 12, 1, 1);
        int y = date.getYear() + 1900;
        System.out.println(y);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar cld = Calendar.getInstance();

        int year = cld.get(Calendar.YEAR);
        System.out.println("年份：" + year);
        System.out.println("月份：" + (cld.get(Calendar.MONTH) + 1));
        System.out.println("日：" + cld.get(Calendar.DAY_OF_MONTH));
    }
}
