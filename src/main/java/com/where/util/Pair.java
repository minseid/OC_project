package com.where.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<F,s> {
    private F first;
    private s second;
}
