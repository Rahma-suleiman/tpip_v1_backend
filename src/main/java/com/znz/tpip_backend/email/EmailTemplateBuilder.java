// package com.znz.tpip_backend.email;

// import org.springframework.stereotype.Component;

// @Component
// public class EmailTemplateBuilder {

//     public String buildRefereeInvitation(String name, String link) {

//         return """
//                 Dear %s,

//                 You have been invited to provide a referee recommendation.

//                 Please click the link below to complete the assessment:
//                 %s

//                 This link is secure and time-limited.

//                 Regards,
//                 TPIP System
//                 """.formatted(name, link);
//     }

//     public String buildReminder(String name, String link) {

//         return """
//                 Reminder: Dear %s,

//                 You have not yet completed your referee recommendation.

//                 Complete it here:
//                 %s

//                 Thank you.
//                 """.formatted(name, link);
//     }
// }
package com.znz.tpip_backend.email;

import org.springframework.stereotype.Component;

@Component
public class EmailTemplateBuilder {

    public String buildRefereeInvitation(String name, String link) {
        return """
                Dear %s,

                You have been invited to submit a referee recommendation.

                Please complete it using the link below:
                %s

                This link is secure and expires automatically.

                Regards,
                TPIP System
                """.formatted(name, link);
    }

    public String buildReminder(String name, String link) {
        return """
                Reminder: Dear %s,

                You still have a pending referee submission.

                Complete it here:
                %s

                Thank you.
                """.formatted(name, link);
    }
}