package uci.atcnea.core.networking;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import main.blibrary.BColor;
import main.command.BoardCommand;
import main.command.ChangeLanguage;
import main.command.CloseApp;
import main.command.EvaluateStudent;
import main.command.GetActivateApps;
import main.command.GetInstalledApps;
import main.command.LessonConnection;
import main.command.LessonConnectionResponse;

import main.command.LessonDisconnection;
import main.command.NotifyFile;
import main.command.OpenApp;
import main.command.PresentationConnection;
import main.command.QuizConnection;
import main.command.SendInteractiveQuestion;
import main.command.SendSummary;
import main.command.ShareScreenCommand;
import main.command.StudentNodeCmd;
import main.command.VideoStreamingAction;
import main.model.AppInfo2;
import uci.atcnea.core.interfaces.RemoteExecute;
import main.blibrary.BCircle;
import main.blibrary.BContainer;
import main.blibrary.BErase;
import main.blibrary.BFree_Pain;
import main.blibrary.BGraphic;
import main.blibrary.BImage;
import main.blibrary.BLine;
import main.blibrary.BPoint;
import main.blibrary.BSquare;
import main.blibrary.BText;

/**
 * Created by yerandy on 3/07/16.
 */
public class Network {

    // This registers objects that are going to be sent over the network.
    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();

/**
 * Nota: El orden y la jerarquía con respecto
 * al llamado de la función contenida en el objeto importan.
 */

//Los tipos de atributos de los comandos se
//deben registrar acá
        ObjectSpace.registerClasses(kryo);
        kryo.register(long.class);
        kryo.register(boolean.class);
        kryo.register(byte[].class);
        kryo.register(int.class);
        kryo.register(String.class);
        kryo.register(AppInfo2.class);
        kryo.register(String[].class);
        kryo.register(List.class);
        kryo.register(Boolean.class);
        kryo.register(RemoteExecute.class);
        kryo.register(OperationResult.class);
        kryo.register(Exception.class);
        kryo.register(Object.class);
        kryo.register(ArrayList.class);
        kryo.register(StackTraceElement[].class);
        kryo.register(Integer.class);
        kryo.register(Object[].class);
        kryo.register(java.lang.NullPointerException.class);
        kryo.register(java.lang.IndexOutOfBoundsException.class);
        kryo.register(StackOverflowError.class);
        kryo.register(LinkedList.class);

//Enum
        kryo.register(LessonDisconnection.Type.class, new EnumNameSerializer(kryo,LessonDisconnection.Type.class));
        kryo.register(LessonDisconnection.DisconnectType.class, new EnumNameSerializer(kryo,LessonDisconnection.DisconnectType.class));
        kryo.register(OperationResult.ResultCode.class, new EnumNameSerializer(kryo,OperationResult.ResultCode.class));
        kryo.register(StudentNodeCmd.BatteryResponseState.class, new EnumNameSerializer(kryo,StudentNodeCmd.BatteryResponseState.class));
        kryo.register(StudentNodeCmd.ItemNodeStatus.class,new EnumNameSerializer(kryo,StudentNodeCmd.ItemNodeStatus.class));
        kryo.register(VideoStreamingAction.ItemNodeStatus.class, new EnumNameSerializer(kryo,VideoStreamingAction.ItemNodeStatus.class));
        kryo.register(QuizConnection.QuizType.class, new EnumNameSerializer(kryo,QuizConnection.QuizType.class));
        kryo.register(PresentationConnection.TypePresentation.class, new EnumNameSerializer(kryo,PresentationConnection.TypePresentation.class));
        kryo.register(BoardCommand.BoardType.class, new EnumNameSerializer(kryo,BoardCommand.BoardType.class));
        kryo.register(BGraphic.BGraphicStatus.class, new EnumNameSerializer(kryo,BGraphic.BGraphicStatus.class));
        kryo.register(BGraphic.FigureType.class, new EnumNameSerializer(kryo,BGraphic.FigureType.class));
        kryo.register(ShareScreenCommand.ShareScreenStatus.class, new EnumNameSerializer(kryo,ShareScreenCommand.ShareScreenStatus.class));

//  kryo.register(SendInteractiveQuestion.TypeInteractiveQuestion.class, new EnumNameSerializer(kryo,SendInteractiveQuestion.TypeInteractiveQuestion.class));

//Los comandos se deben registrar acá
        kryo.register(LessonConnection.class);
        kryo.register(SendInteractiveQuestion.class);
        kryo.register(LessonConnectionResponse.class);
        kryo.register(LessonDisconnection.class);
        kryo.register(StudentNodeCmd.class);
        kryo.register(NotifyFile.class);
        kryo.register(GetActivateApps.class);
        kryo.register(GetInstalledApps.class);
        kryo.register(OpenApp.class);
        kryo.register(CloseApp.class);
        kryo.register(ChangeLanguage.class);
        kryo.register(SendSummary.class);
        kryo.register(QuizConnection.class);
        kryo.register(VideoStreamingAction.class);
        kryo.register(PresentationConnection.class);
        kryo.register(EvaluateStudent.class);
        kryo.register(org.json.JSONException.class);
        kryo.register(BoardCommand.class);
        kryo.register(BColor.class);
        kryo.register(BGraphic.class);
        kryo.register(BErase.class);
        kryo.register(BFree_Pain.class);
        kryo.register(BCircle.class);
        kryo.register(BImage.class);
        kryo.register(BLine.class);
        kryo.register(BPoint.class);
        kryo.register(BSquare.class);
        kryo.register(BText.class);
        kryo.register(BContainer.class);
        kryo.register(ShareScreenCommand.class);
    }


    static public class ChatMessage {
        public String text;

        public ChatMessage() {
        }
    }

    public static class EnumNameSerializer extends Serializer<Enum> {
        private final Class enumType;
        private final Serializer stringSerializer;

        public EnumNameSerializer(Kryo kryo, Class type) {
            this.enumType = type;
            stringSerializer = kryo.getSerializer(String.class);
            setImmutable(true);
        }

        @Override
        public void write(Kryo kryo, Output output, Enum object) {
            kryo.writeObject(output, object.name(), stringSerializer);
        }

        @Override
        public Enum read(Kryo kryo, Input input, Class type) {
            String name = kryo.readObject(input, String.class, stringSerializer);
            try {
                name =  name.replaceAll("[^A-Z_]","");
                return Enum.valueOf(enumType, name);
            } catch (IllegalArgumentException e) {
                 throw new KryoException("Invalid name for enum \"" + enumType.getName() + "\": " + name, e);
            }
        }
    }

}
