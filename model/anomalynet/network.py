import torch
import torch.nn as nn

from utils import _

class LSTMAENet(nn.Module):
    def __init__(self):
        super(LSTMAENet, self).__init__()

        self.encoder = nn.Sequential(
        )

        self.decoder = nn.Sequential(
        )      

    def forward(self, _):
        latent = self.encoder(_)
        result = self.decoder(latent)
        return result