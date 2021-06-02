package cse.maven_webmail.control;

import cse.maven_webmail.model.ReservationMail;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;


public class ReservationHandler extends HttpServlet{

    private static final long serialVersionUID = -4013616887475315494L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationHandler.class);
    @Override
    public void init() {
        try {
            var schedulerFactory = new StdSchedulerFactory();
            var scheduler = schedulerFactory.getScheduler();
            var job1 = new JobDetail("job1", Scheduler.DEFAULT_GROUP, ReservationMail.class);
            var trigger1  = new CronTrigger("job1", Scheduler.DEFAULT_GROUP, "0 0/1 * * * ?");
            scheduler.scheduleJob(job1, trigger1);
            scheduler.start();
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }
    }
}