package org.itzstonlex.recon.fastrecon;

import org.itzstonlex.recon.side.Client;
import org.itzstonlex.recon.side.Server;
import org.itzstonlex.recon.util.FastRecon;

public class MachineTest {

    public static void main(String[] args) {
        Server server = FastRecon.Machine.fastBind("localhost", 1000, null);
        Client client = FastRecon.Machine.fastConnect("localhost",  1000, null);

        System.out.println(server.channel().address());
    }

}
