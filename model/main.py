from anomalynet.dataset import LSTMAEDataset
from anomalynet.model import LSTMAEModel

import torch
import sys
import time
import yaml
from pathlib import Path

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

WORKDIR = Path().absolute()

print("Work directory: %s" % WORKDIR)

with open("./parameters.yaml", "r") as yf:
    parameters = yaml.load(yf, Loader=yaml.SafeLoader)

params = parameters["lstm_ae"]

SAVE_DIR = WORKDIR / "training"



def train():
    start_time = time.time()
    print("----- Starting LSTM AE Train -----")
    print("----- Generating Dataset -----")
    dataset = LSTMAEDataset(batch_size=params["batch_size"])

    print(f"Generated {len(dataset)} samples")
    model = LSTMAEModel(SAVE_DIR, dataset, params, device=device)
    model.train()

    print("Total training time (DualTask):")
    print(time.time() - start_time)
    print("Loss total (DualTask):")
    print(model.history["train_loss_total"])



def run():
    start_time = time.time()
    print("----- Running LSTM AE -----")
    print("----- Generating Dataset -----")
    dataset = LSTMAEDataset(batch_size=params["batch_size"])

    print(f"Generated {len(dataset)} samples")
    model = LSTMAEModel(SAVE_DIR, dataset, params)
    model.load_network(model.save_dir / model.state_dict)


switch = {
    "train": train,
    "run": run
}

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Use: python -m tests.dt (train|run)")
        sys.exit(1)

    action = sys.argv[1]
    if action not in switch.keys():
        print("Use: python -m tests.dt (train|run)")
        sys.exit(1)
    else:
        sys.exit(switch[action]())