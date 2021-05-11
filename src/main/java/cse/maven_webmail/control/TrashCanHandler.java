package cse.maven_webmail.control;

import javax.servlet.http.HttpServletRequest;


public interface TrashCanHandler {

    Boolean moveToTrashCan (HttpServletRequest httpServletRequest);
    Boolean restoreToTrashCan (HttpServletRequest httpServletRequest);
}
