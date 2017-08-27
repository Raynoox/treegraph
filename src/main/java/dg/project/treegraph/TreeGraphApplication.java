package dg.project.treegraph;

import dg.project.treegraph.dao.NodeRepository;
import dg.project.treegraph.models.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TreeGraphApplication implements CommandLineRunner {

	@Autowired
	private NodeRepository repository;
	public static void main(String[] args) {
		SpringApplication.run(TreeGraphApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Node root = new Node.Builder().value(0L).build();
		repository.save(root);
	}
}
