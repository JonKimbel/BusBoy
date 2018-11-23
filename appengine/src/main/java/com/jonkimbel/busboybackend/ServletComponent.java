package com.jonkimbel.busboybackend;

import dagger.Component;

@Component(modules = ServletModule.class)
interface ServletComponent {
  BusBoyServlet servlet();
}
