package uci.atcnea.student.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import main.command.QuizConnection;
import main.command.SendInteractiveQuestion;
import main.command.StudentNodeCmd;
import uci.atcnea.student.MainApp;
import uci.atcnea.student.service.LockScreenService;

/**
 * Created by koyi on 11/05/16.
 */
public class LockScreenThread_TEST extends Thread {

    private Context context;

    public LockScreenThread_TEST(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        super.run();

//        try {Thread.sleep(2000);} catch (Exception e) {e.printStackTrace();}
//
//        context.startService(new Intent(context, LockScreenService.class));
//
//
//        try {Thread.sleep(3000);} catch (Exception e) {e.printStackTrace();}
//        Intent intent = new Intent("MSG_HAND_LOCK_SCREEN");
//        intent.putExtra("GOOD_HAND", true);
//        intent.putExtra("MSG_HAND", "AAAAa BBBBBB VCCCCC");
//        context.sendBroadcast(intent);

//        StudentNodeCmd snc = new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.LOCK_HAND);
//        snc.execute(context);
//
//        try {Thread.sleep(30000);} catch (Exception e) {e.printStackTrace();}
//        context.stopService(new Intent(context, LockScreenService.class));
//
//
//        MainApp.sendBatteryLevel();
//
//        String iq = "{\"data\":{\"typology\":\"int.ques.multiple.choice\",\"time.answ\":\"15\",\"description\":\"De que color era el caballo blanco de Pepe\",\"answer.mode\":\"Pregunta dirigida\",\"time.read\":\"15\"},\"items\":[{\"name\":\"blanco\"},{\"name\":\"blanco\"},{\"name\":\"azul\"},{\"name\":\"verde\"},{\"name\":\"amarillo\"}]}";
//
//        SendInteractiveQuestion siq = new SendInteractiveQuestion(iq, SendInteractiveQuestion.TypeInteractiveQuestion.INTERACTIVE_QUESTION);
//        siq.execute(context);

//        context.startService(new Intent(context, LockScreenService.class));
//
//
//        try {Thread.sleep(15000);} catch (Exception e) {e.printStackTrace();}
//        context.stopService(new Intent(context, LockScreenService.class));
//        context.sendBroadcast(new Intent("CLOSE_LOCK_SCREEN"));


//        StudentNodeCmd s =  new StudentNodeCmd(StudentNodeCmd.ItemNodeStatus.ALLOW_HAND);
//        s.execute(context);


        //Bad
//            String json = "[{\"Attemps\":3,\"Name\":\"examen1\",\"Duration\":1,\"Description\":\"descripcion del examen\",\"Calification\":\"La media\",\"Evaluation\":\"Cuantitativo\"},[\"[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Respuesta simple numerica\",\"tittle\":\"RSN\"},[{\"item\":\"enunciado\",\"response\":\"1\"}]]\",\"[{\"feedback\":\"retoalimentacion\",\"complexity\":\"Difícil\",\"orientation\":\"enunciado\",\"type\":\"Verdadero o falso\",\"tittle\":\"TOF\"},[{\"item\":\"enunciado\",\"response\":\"True\"}]]\",\"[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Seleccion multiple\",\"tittle\":\"SM\"},[{\"item\":\"r1\",\"response\":true},{\"item\":\"r2\",\"response\":false},{\"item\":\"r3\",\"response\":false}]]\",\"[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Seleccion simple\",\"tittle\":\"SS\"},[{\"item\":\"r1\",\"response\":true},{\"item\":\"r2\",\"response\":false},{\"item\":\"r3\",\"response\":false}]]\",\"[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Enlazar columnas\",\"tittle\":\"link\"},[{\"item\":\"p1\",\"itemB\":\"r1\",\"response\":\"3\"},{\"item\":\"p2\",\"itemB\":\"r2\",\"response\":\"2\"},{\"item\":\"p3\",\"itemB\":\"r3\",\"response\":\"1\"}]]\"],[\"1\",\"5\",\"4\",\"3\",\"2\"],{\"idTest\":1}]";

        //Good
//        String json = "[{\"Attemps\":1,\"Name\":\"examen1\",\"Duration\":1,\"Description\":\"descripcion del examen\",\"Calification\":\"La media\",\"Evaluation\":\"Cuantitativo\"},[[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Respuesta simple numerica\",\"tittle\":\"RSN\"},[{\"item\":\"enunciado\",\"response\":\"1\"}]],[{\"feedback\":\"retoalimentacion\",\"complexity\":\"Difícil\",\"orientation\":\"enunciado\",\"type\":\"Verdadero o falso\",\"tittle\":\"TOF\"},[{\"item\":\"enunciado\",\"response\":\"True\"}]],[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Seleccion multiple\",\"tittle\":\"SM\"},[{\"item\":\"r1\",\"response\":true},{\"item\":\"r2\",\"response\":false},{\"item\":\"r3\",\"response\":false}]],[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Seleccion simple\",\"tittle\":\"SS\"},[{\"item\":\"r1\",\"response\":true},{\"item\":\"r2\",\"response\":false},{\"item\":\"r3\",\"response\":false}]],[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Enlazar columnas\",\"tittle\":\"link\"},[{\"item\":\"p1\",\"itemB\":\"r1\",\"response\":\"3\"},{\"item\":\"p2\",\"itemB\":\"r2\",\"response\":\"2\"},{\"item\":\"p3\",\"itemB\":\"r3\",\"response\":\"1\"}]]],[\"1\",\"5\",\"4\",\"3\",\"2\"],{\"idTest\":1}]";
//        String json = "[{\"Attemps\":1,\"Name\":\"examen1\",\"Duration\":1,\"Description\":\"descripcion del examen\",\"Calification\":\"La media\",\"Evaluation\":\"Cuantitativo\"},[[{\"feedback\":\"retroalimentacion\",\"complexity\":\"Muy dificil\",\"orientation\":\"enunciado\",\"type\":\"Enlazar columnas\",\"tittle\":\"link\"},[{\"item\":\"p1\",\"itemB\":\"r1\",\"response\":\"3\"},{\"item\":\"p2\",\"itemB\":\"r2\",\"response\":\"2\"},{\"item\":\"p3\",\"itemB\":\"r3\",\"response\":\"1\"}]]],[\"1\",\"5\",\"4\",\"3\",\"2\"],{\"idTest\":1}]";
        String json = "[{\"Evaluation\":\"Cualitativo\",\"Name\":\"examenprueba\",\"Attemps\":3,\"Description\":\"description\",\"Calification\":\"Promedios de calificaciones\",\"Duration\":10},[[{\"feedback\":\"asdasdasdasdasd\",\"tittle\":\"wdf\",\"orientation\":\"enunciado\",\"type\":\"Verdadero o falso\",\"complexity\":\"Muy fácil\"},[{\"response\":\"True\",\"item\":\"enunciado\"}]],[{\"feedback\":\"asdasdasdasd\",\"tittle\":\"sn\",\"orientation\":\"asdasdasdasd\",\"type\":\"Respuesta simple numerica\",\"complexity\":\"Muy fácil\"},[{\"response\":\"2\",\"item\":\"asdasdasdasd\"}]],[{\"feedback\":\"asdasdasdasd\",\"tittle\":\"asdasdasdasd\",\"orientation\":\"sdasdasdasda\",\"type\":\"Seleccion simple\",\"complexity\":\"Fácil\"},[{\"response\":true,\"item\":\"1\"},{\"response\":false,\"item\":\"2\"},{\"response\":false,\"item\":\"3\"}]],[{\"feedback\":\"asdasdasdasdad\",\"tittle\":\"3\",\"orientation\":\"asdasdasdasd\",\"type\":\"Seleccion multiple\",\"complexity\":\"Fácil\"},[{\"response\":false,\"item\":\"1\"},{\"response\":true,\"item\":\"2\"}]],[{\"feedback\":\"asdadsasdasd\",\"tittle\":\"asdasdasdasd\",\"orientation\":\"asdasdasd\",\"type\":\"Enlazar columnas\",\"complexity\":\"Fácil\"},[{\"response\":\"3\",\"itemB\":\"3\",\"item\":\"1\"},{\"response\":\"2\",\"itemB\":\"2\",\"item\":\"2\"},{\"response\":\"1\",\"itemB\":\"1\",\"item\":\"3\"}]]],[1,2,4,3,5],{\"idTest\":4}]";


        QuizConnection q = new QuizConnection(json, QuizConnection.QuizType.QUIZ);
        q.execute(context);
    }
}
