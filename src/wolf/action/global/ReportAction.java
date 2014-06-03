package wolf.action.global;

import java.util.List;
import java.util.UUID;

import wolf.WolfDB;
import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.Stage;

import com.google.common.base.Joiner;

import ez.DB;
import ez.Row;
import ez.Table;

public class ReportAction extends Action {

  private DB db = WolfDB.get();

  public ReportAction(Stage stage) {
    super(stage, "report", "player", "reason");

    if (!db.hasTable("reports")) {
      db.addTable(new Table("reports").primary("id", UUID.class).column("reporter", String.class).column("offender", String.class)
          .varchar("message", 1024));
    }
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String message = Joiner.on(" ").join(args.subList(1, args.size()));
    if (message.length() > 1024) {
      System.err.println("Got long report: " + message);
      getBot().sendMessage(invoker.getName(), "Please make your report shorter.");
      return;
    }

    if (db == null) {
      System.err.println(invoker.getName() + " REPORTED: " + args);
    } else {
      db.insert("reports", new Row().with("id", UUID.randomUUID()).with("reporter",
          invoker.getName()).with("offender", args.get(0)).with("message", message));
    }
    getBot().sendMessage(invoker.getName(), "Report recorded.");
  }

  @Override
  public String getDescription() {
    return "Report a player for violating a rule or making the game miserable";
  }

  @Override
  protected boolean argSizeMatters() {
    return false;
  }

}
