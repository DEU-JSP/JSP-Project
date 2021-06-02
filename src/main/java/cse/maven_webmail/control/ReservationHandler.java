package cse.maven_webmail.control;

import cse.maven_webmail.model.ReservationMail;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import javax.servlet.http.HttpServlet;


public class ReservationHandler extends HttpServlet{

    private static final long serialVersionUID = -4013616887475315494L;
    private SchedulerFactory schedulerFactory;
    private Scheduler scheduler;

    public ReservationHandler () {
        try {
            schedulerFactory  = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        try {
            JobDetail job1 = new JobDetail("job1", Scheduler.DEFAULT_GROUP, ReservationMail.class);
            CronTrigger trigger1  = new CronTrigger("job1", Scheduler.DEFAULT_GROUP, "0 0/1 * * * ?");
            scheduler.scheduleJob(job1, trigger1);
            scheduler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}