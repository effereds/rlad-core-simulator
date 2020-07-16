# rlad-core-simulator: RL-based Scaling Simulator

Rlad-core is a Reinforcement Learning (RL) based Adaptive Deployment solution, which uses machine learning techniques to scale services. 

This project is a simple simulator to run the rlad-core library. This simulator allows to model an application and evaluate its runtime adaptation. The runtime adaption is controlled by means of different techniques, including those presented in the rlad-core library, namely Q-learning and Model-based. 

Further details regarding our RL-based solutions can be found in our papers. 

## Cite Us

This simulator was first used in our paper published in the [Proceedings of the 2019 IEEE International Conference on Cloud Computing (CLOUD 2019)](https://ieeexplore.ieee.org/document/8814555). 

If you use our simulator, please cite the following publication: 

```
@InProceedings{RoNaCa:2019,
  author={F. {Rossi} and M. {Nardelli} and V. {Cardellini}},
  booktitle={Proceedings of the 2019 IEEE International Conference on Cloud Computing (CLOUD 2019)}, 
  title={Horizontal and Vertical Scaling of Container-Based Applications Using Reinforcement Learning}, 
  year={2019},
  pages={329-338}
}
```

## Our Works

A short list of our works that used this simulator:

- F. Rossi, V. Cardellini, F. Lo Presti, M. Nardelli, "Geo-distributed efficient deployment of containers with Kubernetes", Computer Communications, 14 pages, vol.159, pp.161--174, June 2020

- F. Rossi, V. Cardellini, F. Lo Presti, "Elastic Deployment of Software Containers in Geo-Distributed Computing Environments" In Proceedings of the 2019 IEEE Symposium on Computers and Communications (ISCC 2019), pp. 1-7, Barcelona, Spain, June 29-July 3 2019.

- F. Rossi, M. Nardelli, V. Cardellini, "Horizontal and Vertical Scaling of Container-based Applications using Reinforcement Learning" In Proceedings of the 2019 IEEE International Conference on Cloud Computing (CLOUD 2019), pp. 329-338, Milan, Italy, July 8-13 2019. 

A more extensive list of our works can be found [on our website](http://www.ce.uniroma2.it/~fabiana/#publications).


## Background 

### Reinforcement learning

RL strategies aim to learn what to do (i.e., how to map situations to actions), so to minimize a numerical cost signal. To minimize the obtained cost, a RL agent must prefer actions, tried in the past, that it found to be effective (_exploitation_). 
However, to discover such actions, it has to explore new actions (_exploration_). For each application, we consider a RL agent that is in charge of adapting at run-time the application deployment with the aim of minimizing a long-term cost.

The RL agent interacts with the application in discrete time steps. At each time step, the agent observes the application state and performs an action. One time step later, the application transits in a new state, causing the payment of an immediate cost. Both the paid cost and the next state transition usually depend on external unknown factors. To minimize the expected long-term cost, the agent estimates the so-called Q-function. 


### RLAD Core: Main Concepts

The service is represented through the Service interface which exposes methods to recover service response time, CPU utilization, and to update the number of service instances and the percentage of CPU assigned to each service instance. Each service is modeled as a configurable MGN queue. 

The RLAgent class realizes the behavior of the RL agent. The agent (one for each application service) may be able to detect the status of the service, choose and perform actions to reach configurable Quality of Service Requirements. 

The execution of action a in state s leads the transition in a new state and also the payment of an immediate cost. The immediate cost is computed by the RewardProvider. The RLAgent class provides the attribute `qTable`, that is an array of size `{#states} × {#actions}` that stores the Q-value for each state and action pair. 

Since the updating of the Q (s, a) value depends on the specific RL algorithm, the RLAgent abstract class is implemented by the concrete classes QLearningAgent, DynaQAgent and ModelBasedAgent.

We provide Java classes that allow you to simulate horizontal and/or vertical scaling of a service. The Action class allows to choose an action that involves the scale-in or scale-out of service instances.
The Action class is extended by the ActionHorizontalOrVertical and ActionHorizontalAndVertical. 

- With the ActionHorizontalOrVertical class, it is possible to simulate the execution of one of the following actions: adding a service instance (scale-out), removing a service instance (scale-in), adding a percentage of the CPU assigned to each service instances (scale-up), reduction of the CPU assigned to the service (scale-down).

- With the ActionHorizontalAndVertical, both the number of containers and the amount of CPU assigned to the service can be updated.


## Usage 

To start a new simulation, we should first choose the configuration parameters; see the `Simulator` and `Configuration` classes.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## Authors 

- [Fabiana Rossi](http://www.ce.uniroma2.it/~fabiana/)
- [Matteo Nardelli](http://www.ce.uniroma2.it/~nardelli/)
- [Valeria Cardellini](http://www.ce.uniroma2.it/~valeria/)
- [Francesco Lo Presti](http://www.ce.uniroma2.it/~lopresti/)

## License
[GNU v3](LICENSE.md)

