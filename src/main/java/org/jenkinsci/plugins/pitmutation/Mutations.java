package org.jenkinsci.plugins.pitmutation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Mutations {
    private List<Mutation> mutation;
}
